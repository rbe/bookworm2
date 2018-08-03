/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

@Component
final class HoerbuchkatalogImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogImporter.class);

    private final RepositoryConfig repositoryConfig;

    private final RepositoryArchiv repositoryArchiv;

    private final HoerbuchMapper hoerbuchMapper;

    @Autowired
    public HoerbuchkatalogImporter(final RepositoryConfig repositoryConfig,
                                   final RepositoryArchiv repositoryArchiv,
                                   final HoerbuchMapper hoerbuchMapper) {
        this.repositoryConfig = repositoryConfig;
        this.repositoryArchiv = repositoryArchiv;
        this.hoerbuchMapper = hoerbuchMapper;
    }

    Set<Hoerbuch> importiereKatalogAusArchiv() throws ImportFailedException {
        final Path fileName = repositoryArchiv.maybeFindeAktuellstenKatalog("Gesamt.dat");
        Set<Hoerbuch> hoerbuecher = null;
        if (null != fileName) {
            hoerbuecher = importiereKatalog(fileName.getFileName());
        } else {
            LOGGER.warn("Kein Hörbuchkatalog im Archiv gefunden");
        }
        return null != hoerbuecher ? hoerbuecher : Collections.emptySet();
    }

    void aktualisiereKatalogImArchiv() throws ImportFailedException {
        LOGGER.info("Aktualisiere Hörbuchkatalog");
        final Path hoerbuchkatalogDirectory = repositoryConfig.getHoerbuchkatalogDirectory();
        final Path gesamtdat = hoerbuchkatalogDirectory.resolve(repositoryConfig.getWbhGesamtdatFilename());
        if (Files.exists(gesamtdat)) {
            try {
                repositoryArchiv.archiviereKatalog(gesamtdat);
            } catch (ArchiveFailedException e) {
                throw new ImportFailedException(e);
            }
        } else {
            throw new ImportFailedException(String.format("Keine neue %s gefunden", gesamtdat));
        }
    }

    private Set<Hoerbuch> importiereKatalog(Path fileName) throws ImportFailedException {
        final Path hoerbuchkatalogDirectory = repositoryConfig.getHoerbuchkatalogDirectory();
        final Path gesamtDat = hoerbuchkatalogDirectory.resolve(fileName);
        LOGGER.info("Importiere Hörbücher aus {}", gesamtDat);
        try {
            final Set<Hoerbuch> hoerbuecher = hoerbuchMapper.parse(gesamtDat);
            LOGGER.info("Insgesamt {} Hörbücher importiert", hoerbuecher.size());
            return hoerbuecher;
        } catch (IOException e) {
            throw new ImportFailedException(e);
        }
    }

}
