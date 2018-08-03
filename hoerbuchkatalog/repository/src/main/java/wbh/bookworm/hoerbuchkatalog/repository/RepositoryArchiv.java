/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
final class RepositoryArchiv {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryArchiv.class);

    private final RepositoryConfig repositoryConfig;

    @Autowired
    RepositoryArchiv(final RepositoryConfig repositoryConfig) {
        this.repositoryConfig = repositoryConfig;
    }

    void createDirectoryOrFail(final Path directory) {
        final boolean directoryExists = directory.toFile().exists();
        if (!directoryExists) {
            final boolean directoriesCreated = directory.toFile().mkdirs();
            if (!directoriesCreated) {
                LOGGER.error("Kann Verzeichnis {} nicht erstellen", directory.toAbsolutePath());
                throw new RuntimeException();
            }
        }
        LOGGER.info("Archiv-Verzeichnis ist {}", directory.toAbsolutePath());
    }

    void archiviereKatalog(final Path fromFile) throws ArchiveFailedException {
        final Path toDirectory = repositoryConfig.getHoerbuchkatalogDirectory();
        final String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        final String archivedFilename = String.format("%s.%s", fromFile.getFileName().toString(), today);
        final Path archivedPath = toDirectory.resolve(archivedFilename).toAbsolutePath();
        try {
            Files.move(fromFile, archivedPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Katalog {} archiviert nach {}", fromFile, archivedPath);
        } catch (IOException e) {
            throw new ArchiveFailedException(String.format("Kann Katalog %s nicht archivieren", fromFile), e);
        }
    }

    Path maybeFindeAktuellstenKatalog(final String katalog) {
        final Path directory = repositoryConfig.getHoerbuchkatalogDirectory();
        try (final Stream<Path> paths = Files.list(directory)
                .filter(p -> p.getFileName().toString().startsWith(katalog))
                .sorted()) {
            final List<Path> collect = paths.collect(Collectors.toList());
            if (!collect.isEmpty()) {
                Collections.reverse(collect);
                return collect.get(0);
            } else {
                return null;
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Kann aktuellsten Katalog für %s nicht finden", katalog), e);
        }
        throw new IllegalStateException();
    }

}
