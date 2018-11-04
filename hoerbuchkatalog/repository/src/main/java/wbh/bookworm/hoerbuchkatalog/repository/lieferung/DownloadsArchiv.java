/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import aoc.ddd.repository.RepositoryArchive;
import aoc.ddd.repository.RepositoryArchiveException;
import aoc.ddd.spring.Singleton;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Optional;

@Singleton
final class DownloadsArchiv {

    private final RepositoryArchive repositoryArchive;

    @Autowired
    DownloadsArchiv(final DownloadsConfig downloadsConfig) {
        this.repositoryArchive = new RepositoryArchive(
                downloadsConfig.getBlistaDlsDirectory());
    }

    void archiviere(final Path katalogDatei) {
        try {
            repositoryArchive.archive(katalogDatei);
        } catch (RepositoryArchiveException e) {
            throw new BlistaDlsException(String.format(
                    "Kann Katalog '%s' nicht archivieren", katalogDatei), e);
        }
    }

    Optional<Path> findeAktuellstenKatalog(final String katalog) {
        try {
            return repositoryArchive.find(katalog);
        } catch (RepositoryArchiveException e) {
            throw new BlistaDlsException(String.format(
                    "Kann Katalog '%s' nicht im Archiv finden", katalog), e);
        }
    }

}
