/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import aoc.ddd.repository.RepositoryArchive;
import aoc.ddd.repository.RepositoryArchiveException;
import aoc.ddd.spring.Singleton;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Optional;

@Singleton
final class HoerbuchkatalogArchiv {

    private final RepositoryArchive repositoryArchive;

    @Autowired
    HoerbuchkatalogArchiv(final HoerbuchkatalogConfig hoerbuchkatalogConfig) {
        this.repositoryArchive = new RepositoryArchive(
                hoerbuchkatalogConfig.getHoerbuchkatalogDirectory());
    }

    void archiviereKatalog(final Path katalogDatei) {
        try {
            repositoryArchive.archive(katalogDatei);
        } catch (RepositoryArchiveException e) {
            throw new HoerbuchkatalogArchivException(String.format(
                    "Kann Katalog '%s' nicht archivieren", katalogDatei), e);
        }
    }

    Optional<Path> findeAktuellstenKatalog(final String katalog) {
        try {
            return repositoryArchive.find(katalog);
        } catch (RepositoryArchiveException e) {
            throw new HoerbuchkatalogArchivException(String.format(
                    "Kann Katalog '%s' nicht im Archiv finden", katalog), e);
        }
    }

}
