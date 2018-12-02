/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RepositoryArchive {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryArchive.class);

    private final Path archiveDirectory;

    public RepositoryArchive(final Path archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
        createDirectoryOrFail();
    }

    private void createDirectoryOrFail() {
        final boolean directoryExists = Files.exists(archiveDirectory);
        if (!directoryExists) {
            try {
                Files.createDirectories(archiveDirectory);
            } catch (IOException e) {
                throw new RuntimeException(String.format(
                        "Cannot create directory '%s'", archiveDirectory.toAbsolutePath()));
            }
        }
        LOGGER.info("Archive directory is '{}'", archiveDirectory.toAbsolutePath());
    }

    private String[] filenameAndExtension(final Path filename) {
        final String[] splitByDot = filename.getFileName().toString().split("[.]");
        final String name = String.join(".",
                Arrays.copyOfRange(splitByDot, 0, splitByDot.length - 1));
        final String extension = splitByDot[splitByDot.length - 1];
        return new String[]{name, extension};
    }

    public Path archive(final Path fromFile) throws RepositoryArchiveException {
        final String[] fext = filenameAndExtension(fromFile);
        final String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .replaceAll("[.]", "-")
                .replaceAll(":", "-");
        final String archivedFilename = String.format("%s-%s.%s", fext[0], timestamp, fext[1]);
        final Path archivedFile = archiveDirectory.resolve(archivedFilename).toAbsolutePath();
        try {
            Files.move(fromFile, archivedFile, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Archived file '{}' to '{}'", fromFile, archivedFile);
            return archivedFile;
        } catch (IOException e) {
            throw new RepositoryArchiveException(String.format("Canot archive file '%s'", fromFile), e);
        }
    }

    public boolean exists(final Path name) {
        return Files.exists(archiveDirectory.resolve(name));
    }

    public Optional<Path> find(final Path name) throws RepositoryArchiveException {
        final String[] fext = filenameAndExtension(name);
        try (final Stream<Path> paths = Files.list(archiveDirectory)
                .filter(p -> p.getFileName().toString().startsWith(fext[0])
                        && p.getFileName().toString().endsWith(fext[1]))
                .sorted()) {
            final List<Path> collect = paths.collect(Collectors.toList());
            if (!collect.isEmpty()) {
                Collections.reverse(collect);
                return Optional.of(collect.get(0));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RepositoryArchiveException(String.format(
                    "Cannot find most recent for '%s' in archive", name), e);
        }
    }

    public static void main(String[] args) throws RepositoryArchiveException {
        final RepositoryArchive repositoryArchive = new RepositoryArchive(Path.of("/Users/rbe/project/wbh.bookworm/hoerbuchkatalog/assembly/var/hoerbuchkatalog"));
        System.out.printf("%s%n", repositoryArchive.find(Path.of("Gesamt.dat")));
        System.out.printf("%s%n", repositoryArchive.exists(Path.of("Gesamt.dat")));
    }

}
