/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import aoc.mikrokosmos.ddd.repository.RepositoryArchive;
import aoc.mikrokosmos.ddd.repository.RepositoryArchiveException;
import aoc.mikrokosmos.ddd.spring.Singleton;

@Singleton
final class HoerbuchkatalogArchiv {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogArchiv.class);

    private final RepositoryArchive repositoryArchive;

    @Autowired
    HoerbuchkatalogArchiv(final HoerbuchkatalogConfig hoerbuchkatalogConfig) {
        this.repositoryArchive = new RepositoryArchive(hoerbuchkatalogConfig.getDirectory());
    }

    Path archiviereKatalog(final Path katalogDatei) {
        try {
            return repositoryArchive.archive(katalogDatei);
        } catch (RepositoryArchiveException e) {
            throw new HoerbuchkatalogArchivException(String.format(
                    "Kann Katalog '%s' nicht archivieren", katalogDatei), e);
        }
    }

    Optional<Path> findeAktuellstenKatalog(final Path filename) {
        try {
            return repositoryArchive.find(filename);
        } catch (RepositoryArchiveException e) {
            throw new HoerbuchkatalogArchivException(String.format(
                    "Kann Katalog '%s' nicht im Archiv finden", filename), e);
        }
    }

    void archiviereNeuenKatalog(final Path filename) {
        if (repositoryArchive.exists(filename)) {
            LOGGER.info("Aktualisiere Archiv des Hörbuchkatalogs mit '{}'", filename);
            archiviereKatalog(filename);
        }
    }

}
