/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class DirectoryState {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryState.class);

    private final Set<Path> ignoredFiles = new TreeSet<>(Arrays.asList(
            Path.of(".DS_Store")
    ));

    private final Path directory;

    private final Map<Path, FileState> fileStates;

    DirectoryState(final Path directory) {
        this.directory = directory;
        fileStates = new ConcurrentHashMap<>();
        scanDirectory();
    }

    private void scanDirectory() {
        try (final Stream<Path> stream = Files.list(directory)
                .filter(f -> !ignoredFiles.contains(f.getFileName()))) {
            stream.forEach(this::put);
        } catch (IOException e) {
            LOGGER.error("Could not scan directory for existing files", e);
        }
    }

    public Path getDirectory() {
        return directory;
    }

    public void addIgnoredFiles(Path... ignoredFilenames) {
        ignoredFiles.addAll(Arrays.asList(ignoredFilenames));
    }

    public FileState get(final Path path) {
        return fileStates.getOrDefault(path, null);
    }

    public void put(final Path path) {
        fileStates.putIfAbsent(path, new FileState(path));
    }

    public void remove(final Path path) {
        fileStates.remove(path);
    }

    public boolean hasPaths() {
        return !fileStates.isEmpty();
    }

    public Set<Path> paths() {
        return fileStates.keySet();
    }

    public void forgetFiles() {
        fileStates.clear();
    }

    @Override
    public String toString() {
        return String.format("DirectoryState{directory=%s,fileStates=%s}",
                directory, fileStates);
    }

}
