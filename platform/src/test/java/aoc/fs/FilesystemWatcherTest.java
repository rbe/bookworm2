/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.fs;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilesystemWatcherTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemWatcherTest.class);

    @Test
    void shouldEvaluateFilesCompleteSpecification() {
        final Path directory = createDirectory();
        final FilesystemWatcher filesystemWatcher = new FilesystemWatcher(directory);
        final AtomicBoolean b = new AtomicBoolean(false);
        final Path mytestTxt = directory.resolve("Mytest.txt");
        filesystemWatcher.registerFilesCompleteListener(
                Collections.singleton(mytestTxt.getFileName()), 5, TimeUnit.SECONDS,
                path -> {
                    LOGGER.info("Callback for {}", path);
                    b.set(true);
                });
        final Thread thread = startThread(filesystemWatcher);
        try {
            LOGGER.info("Creating {}", mytestTxt);
            Files.write(mytestTxt, "Test".getBytes());
            waitFor(15, TimeUnit.SECONDS);
            assertTrue(b.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            stopFilesystemWatcher(filesystemWatcher, thread);
            delete(mytestTxt);
        }
    }

    @Test
    void shouldFailEvaluateSpecification() {
        final Path directory = createDirectory();
        final FilesystemWatcher filesystemWatcher = new FilesystemWatcher(directory);
        final AtomicBoolean b = new AtomicBoolean(false);
        final Path file = directory.resolve("Mytest.txt");
        delete(file);
        filesystemWatcher.registerFilesCompleteListener(
                Collections.singleton(file.getFileName()), 5, TimeUnit.SECONDS,
                path -> {
                    LOGGER.info("Callback for {}", path);
                    b.set(true);
                });
        final Thread thread = startThread(filesystemWatcher);
        waitFor(15, TimeUnit.SECONDS);
        assertFalse(b.get());
        stopFilesystemWatcher(filesystemWatcher, thread);
        delete(file);
    }

    @Test
    void shouldSeeCreatedFile() {
        final Path directory = createDirectory();
        final FilesystemWatcher filesystemWatcher = new FilesystemWatcher(directory);
        final AtomicBoolean b = new AtomicBoolean(false);
        filesystemWatcher.register(path -> b.set(true));
        final Thread thread = startThread(filesystemWatcher);
        final Path subject = writeTemporaryFile(directory);
        waitFor(15, TimeUnit.SECONDS);
        assertTrue(b.get());
        stopFilesystemWatcher(filesystemWatcher, thread);
        delete(subject);
    }

    private void delete(final Path file) {
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path writeTemporaryFile(final Path directory) {
        try {
            final Path subject = Files.createTempFile(directory,
                    this.getClass().getSimpleName(), ".txt");
            LOGGER.info("Created {}", subject);
            Files.write(subject, "Test".getBytes());
            return subject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path createDirectory() {
        final Path directory = Paths.get("target/" + this.getClass().getSimpleName());
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return directory;
    }

    private void waitFor(long timeout, TimeUnit timeUnit) {
        LOGGER.info("Waiting for {} seconds", timeout);
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Thread startThread(final FilesystemWatcher filesystemWatcher) {
        final Thread thread = new Thread(filesystemWatcher);
        thread.start();
        LOGGER.info("{} started", filesystemWatcher);
        return thread;
    }

    private void stopFilesystemWatcher(final FilesystemWatcher filesystemWatcher, final Thread thread) {
        LOGGER.info("Asking {} to stop", filesystemWatcher);
        filesystemWatcher.pleaseStop();
        try {
            thread.join();
            LOGGER.info("{} stopped", filesystemWatcher);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
