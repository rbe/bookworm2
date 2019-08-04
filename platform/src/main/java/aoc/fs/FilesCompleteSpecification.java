/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.fs;

import aoc.ddd.specification.Specification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class FilesCompleteSpecification implements Specification<DirectoryState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesCompleteSpecification.class);

    private final Set<Path> neededFiles;

    private final long timeout;

    private final TimeUnit timeUnit;

    public FilesCompleteSpecification(final Set<Path> neededFiles,
                                      final long timeout, final TimeUnit timeUnit) {
        this.neededFiles = new TreeSet<>(neededFiles);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public boolean isSatisfied(final DirectoryState candidate) {
        final Path[] paths = candidate.paths().toArray(Path[]::new);
        final boolean filesAreComplete = Arrays.stream(paths)
                .map(Path::getFileName)
                .peek(p -> LOGGER.trace("filesAreComplete: {}", p))
                .collect(Collectors.toSet())
                .containsAll(neededFiles);
        final boolean filesNotModifiedFor = Arrays.stream(paths)
                .peek(p -> LOGGER.trace("filesNotModifiedFor: {}", p.getFileName()))
                .allMatch(p -> isNotModifiedFor(p, timeout, timeUnit));
        return filesAreComplete && filesNotModifiedFor;
    }

    private boolean isNotModifiedFor(final Path path, final long timeout, final TimeUnit timeUnit) {
        try {
            return Instant.now().minusMillis(timeUnit.toMillis(timeout))
                    .isAfter(Files.getLastModifiedTime(path).toInstant());
        } catch (IOException e) {
            LOGGER.debug("Cannot get lastModifiedTime for {}", path);
            return false;
        }
    }

}
