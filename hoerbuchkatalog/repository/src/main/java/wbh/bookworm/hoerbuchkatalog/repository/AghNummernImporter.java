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
import org.springframework.stereotype.Service;
import wbh.bookworm.hoerbuchkatalog.domain.AghNummer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
final class AghNummernImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AghNummernImporter.class);

    private final RepositoryConfig repositoryConfig;

    private final RepositoryArchiv repositoryArchiv;

    @Autowired
    AghNummernImporter(final RepositoryConfig repositoryConfig,
                       final RepositoryArchiv repositoryArchiv) {
        this.repositoryConfig = repositoryConfig;
        this.repositoryArchiv = repositoryArchiv;
    }

    Set<AghNummer> importiereKatalogAusArchiv() throws ImportFailedException {
        final Path fileName = repositoryArchiv.maybeFindeAktuellstenKatalog("isofiles.zip");
        if (null != fileName) {
            return importiereKatalog(fileName);
        } else {
            LOGGER.warn("Keine AGH Nummern im Archiv gefunden");
            return Collections.emptySet();
        }
    }

    void aktualisiereKatalogImArchiv() throws ImportFailedException {
        final String url = repositoryConfig.getBlistaDlsCatalogRestUrl();
        LOGGER.info("Aktualisiere AGH Nummern von {}", url);
        try {
            final Path downloadPath = DownloadHelper.downloadUsingHttpURLConnection(url);
            if (Files.exists(downloadPath)) {
                final Path isofilesZip = downloadPath.getParent().resolve("isofiles.zip");
                Files.move(downloadPath, isofilesZip);
                repositoryArchiv.archiviereKatalog(isofilesZip);
            } else {
                throw new ImportFailedException(String.format("Datei (%s) mit AGH Nummern existiert nach Download nicht", downloadPath));
            }
        } catch (Exception e) {
            throw new ImportFailedException("Kann AGH Nummern nicht aktualisieren", e);
        }
    }

    private Set<AghNummer> importiereKatalog(final Path katalogZip) throws ImportFailedException {
        LOGGER.info("Importiere AGH Nummern aus {}", katalogZip);
        final Set<AghNummer> aghNummern = new TreeSet<>(AghNummer::compareTo);
        try {
            final String pathInZip = repositoryConfig.getBlistaDlsCatalogAghNummernPathInZip();
            final List<String> neueAghNummern = DownloadHelper.extractLinesFromPathInZip(katalogZip, pathInZip);
            final List<String> aghNummernOhneErsteZeile = neueAghNummern.subList(1, neueAghNummern.size());
            aghNummernOhneErsteZeile.forEach(a -> {
                LOGGER.trace("Neue AGH Nummer: {}", a);
                aghNummern.add(new AghNummer(a));
            });
            LOGGER.info("Insgesamt {} AGH Nummern importiert", aghNummern.size());
        } catch (Exception e) {
            throw new ImportFailedException(e);
        }
        return aghNummern;
    }

}
