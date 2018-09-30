/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.platform.ddd.repository.model.DomainRespositoryComponent;
import wbh.bookworm.platform.http.DownloadHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@DomainRespositoryComponent
class AghNummernRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AghNummernRepository.class);

    private final HoerbuchkatalogConfig hoerbuchkatalogConfig;

    private final RepositoryArchiv repositoryArchiv;

    @Autowired
    AghNummernRepository(final HoerbuchkatalogConfig hoerbuchkatalogConfig,
                         final RepositoryArchiv repositoryArchiv) {
        this.hoerbuchkatalogConfig = hoerbuchkatalogConfig;
        this.repositoryArchiv = repositoryArchiv;
    }

    Set<AghNummer> importiereKatalogAusArchiv() throws ImportFailedException {
        final Optional<Path> fileName = repositoryArchiv.findeAktuellstenKatalog("isofiles.zip");
        if (fileName.isPresent()) {
            return importiereKatalog(fileName.get());
        } else {
            LOGGER.warn("Keine AGH Nummern im Archiv gefunden");
            return Collections.emptySet();
        }
    }

    void aktualisiereKatalogImArchiv() throws ImportFailedException {
        final String url = hoerbuchkatalogConfig.getBlistaDlsCatalogRestUrl();
        LOGGER.info("Aktualisiere AGH Nummern von {}", url);
        try {
            final Path downloadPath = DownloadHelper.downloadUsingHttpsURLConnection(url);
            if (Files.exists(downloadPath)) {
                final Path isofilesZip = downloadPath.getParent().resolve("isofiles.zip");
                Files.move(downloadPath, isofilesZip);
                repositoryArchiv.archiviereKatalog(isofilesZip);
            } else {
                final String message =
                        String.format("Datei (%s) mit AGH Nummern existiert nach Download nicht",
                                downloadPath);
                throw new ImportFailedException(message);
            }
        } catch (Exception e) {
            throw new ImportFailedException("Kann AGH Nummern nicht aktualisieren", e);
        }
    }

    private Set<AghNummer> importiereKatalog(final Path katalogZip) throws ImportFailedException {
        LOGGER.info("Importiere AGH Nummern aus {}", katalogZip);
        final Set<AghNummer> aghNummern = new TreeSet<>(AghNummer::compareTo);
        try {
            final String pathInZip = hoerbuchkatalogConfig.getBlistaDlsCatalogAghNummernPathInZip();
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
