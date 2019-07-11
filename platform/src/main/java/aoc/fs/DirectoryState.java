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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class DirectoryState {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryState.class);

    private static final List<Pattern> IGNORED_FILES = Arrays.asList(
            Pattern.compile("\\.DS_Store"),
            Pattern.compile(".*\\.filepart"),
            Pattern.compile(".*\\.t[e]mp")
    );

    static final Predicate<Path> IS_IGNORED = path ->
            IGNORED_FILES.stream().anyMatch(p -> p.matcher(path.getFileName().toString()).matches());

    private final Path directory;

    private final Map<Path, FileState> fileStates;

    DirectoryState(final Path directory) {
        this.directory = directory;
        fileStates = new ConcurrentHashMap<>();
        scanDirectory();
    }

    private void scanDirectory() {
        try (final Stream<Path> stream = Files.list(directory)
                .filter(p -> !IS_IGNORED.test(p))) {
            stream.forEach(this::put);
        } catch (IOException e) {
            LOGGER.error("Could not scan directory for existing files", e);
        }
    }

    public Path getDirectory() {
        return directory;
    }

    public void addIgnoredFiles(String... ignoredFilenames) {
        IGNORED_FILES.addAll(Arrays.asList(
                Arrays.stream(ignoredFilenames)
                        .map(Pattern::compile)
                        .toArray(Pattern[]::new))
        );
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
        LOGGER.debug("Forgetting all watched file states ({})", fileStates);
        fileStates.clear();
    }

    @Override
    public String toString() {
        return String.format("DirectoryState{directory=%s,fileStates=%s}",
                directory, fileStates);
    }

}
