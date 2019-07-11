/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.fs;

import aoc.ddd.specification.Specification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Component
@Lazy
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public final class FilesystemWatcher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemWatcher.class);

    private final Path directory;

    private final WatchService watchService;

    private final DirectoryState directoryState;

    public FilesystemWatcher(final Path directory) {
        Objects.requireNonNull(directory);
        LOGGER.trace("Initializing for {}", directory);
        this.directory = directory;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            directoryState = new DirectoryState(directory);
            registerDirectory(directory);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        filesystemEventListeners = new ArrayList<>();
        specificationListeners = new HashMap<>();
    }

    private void registerDirectory(final Path directory) {
        final WatchKey register;
        try {
            register = directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            LOGGER.trace("{}: {}", register, register.isValid());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void processWatchKey(final WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            final Path eventPath = directory.resolve((Path) event.context());
            if (!DirectoryState.IS_IGNORED.test(eventPath)) {
                LOGGER.debug("{} {}", event.kind(), eventPath);
                WatchEvent.Kind<?> kind = event.kind();
                if (ENTRY_CREATE.equals(kind) || ENTRY_MODIFY.equals(kind)) {
                    directoryState.put(eventPath);
                } else if (ENTRY_DELETE.equals(kind)) {
                    directoryState.remove(eventPath);
                }
                fireFilesystemChangeEvent(eventPath);
                checkAndFireSpecifications();
            } else {
                LOGGER.debug("Ignoring {} as its contained in our ignoredFiles",
                        eventPath);
            }
        }
    }

    private List<FilesystemEventListener> filesystemEventListeners;

    public void register(final FilesystemEventListener fileSystemEventListener) {
        filesystemEventListeners.add(fileSystemEventListener);
    }

    private void fireFilesystemChangeEvent(final Path path) {
        filesystemEventListeners.forEach(l -> {
            LOGGER.trace("Firing FilesystemEvent callback to {}",
                    l);
            try {
                l.processFilesystemEvent(path);
            } catch (Exception e) {
                LOGGER.error("FilesystemEvent callback failed", e);
            }
        });
    }

    private Map<Specification<DirectoryState>, List<SpecificationSatisfiedListener>> specificationListeners;

    public void register(final Specification<DirectoryState> specification,
                         final SpecificationSatisfiedListener specificationSatisfiedListener) {
        specificationListeners.put(specification,
                Collections.singletonList(specificationSatisfiedListener));
    }

    public void registerFilesCompleteListener(final Set<Path> neededFiles,
                                              final long timeout, final TimeUnit timeUnit,
                                              final SpecificationSatisfiedListener specificationSatisfiedListener) {
        specificationListeners.put(new FilesCompleteSpecification(neededFiles, timeout, timeUnit),
                Collections.singletonList(specificationSatisfiedListener));
    }

    private void checkAndFireSpecifications() {
        synchronized (directoryState) {
            specificationListeners.forEach((spec, fel) -> {
                if (spec.isSatisfied(directoryState)) {
                    fel.forEach(l -> {
                        LOGGER.trace("Firing SpecificationSatisfied callback to {}", fel);
                        try {
                            l.processSpecificationSatistied(directoryState);
                        } catch (Exception e) {
                            LOGGER.error("SpecifiactionSatisfied callback failed", e);
                        }
                    });
                }
            });
        }
    }

    private volatile boolean stopRequested;

    public void pleaseStop() {
        LOGGER.debug("Stop requested!");
        this.stopRequested = true;
    }

    private volatile boolean stopped;

    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void run() {
        WatchKey watchKey;
        int count = 0;
        while (!stopRequested) {
            try {
                watchKey = watchService.poll(1, TimeUnit.SECONDS);
                if (null != watchKey) {
                    processWatchKey(watchKey/*, directory*/);
                    /*boolean valid = */watchKey.reset();
                }
                count++;
            } catch (InterruptedException e) {
                LOGGER.debug("Interrupted");
                Thread.currentThread().interrupt();
            }
            // Check specifications every 5 * poll(timeout, TimeUnit), see above
            if (count == 5) {
                checkAndFireSpecifications();
                count = 0;
            }
        }
        LOGGER.debug("Closing WatchService");
        try {
            watchService.close();
        } catch (IOException e) {
            LOGGER.warn("Could not close WatchService", e);
        }
        stopped = true;
    }

}
