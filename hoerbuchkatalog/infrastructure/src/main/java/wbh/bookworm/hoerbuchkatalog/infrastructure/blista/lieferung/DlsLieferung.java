/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsAntwort;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsBook;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsFehlermeldung;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsRestConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsWerke;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.RestServiceClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Blista DLS
 * Agiert als ein Anti-Corruption-Layer?? für Bestellungen -> domain.BlistaDownloads/BlistaDownload
 */
@Service
public class DlsLieferung {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlsLieferung.class);

    private final DlsRestConfig dlsRestConfig;

    @Autowired
    public DlsLieferung(final DlsRestConfig dlsRestConfig) {
        this.dlsRestConfig = dlsRestConfig;
    }

    public Optional<DlsWerke> alleWerkeLaden(final String hoerernummer) {
        try {
            final URL url = new URL(String.format("%s/%s", dlsRestConfig.getWerkeurl(),
                    hoerernummer));
            final byte[] antwort = abfrageSendenUndAntwortArchivieren(hoerernummer, url);
            if (antwort.length > 0) {
                final DlsAntwort dlsAntwort = RestServiceClient.werteAntwortAus(antwort);
                if (dlsAntwort instanceof DlsWerke) {
                    return Optional.of((DlsWerke) dlsAntwort);
                } else if (dlsAntwort instanceof DlsFehlermeldung) {
                    final DlsWerke dlsWerke = new DlsWerke();
                    dlsWerke.dlsFehlermeldung = (DlsFehlermeldung) dlsAntwort;
                    return Optional.of(dlsWerke);
                } else {
                    LOGGER.error("Antwort des blista DLS kein bekannter Antworttyp: {}", dlsAntwort);
                    return Optional.empty();
                }
            } else {
                LOGGER.warn("Antwort des blista DLS hat falsche Länge: {}", antwort.length);
                return Optional.empty();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    public Optional<DlsBook> bestellungLaden(final String hoerernummer, final String aghNummer) {
        final long startBook = System.nanoTime();
        try {
            // TODO WICHTIG Eindeutige URL für Bestellung erzeugen, bspw. AGH Nummer + Datum?
            final URL url = new URL(String.format("%s/%s/%s", dlsRestConfig.getWerkeurl(),
                    hoerernummer, aghNummer));
            final byte[] antwort = abfrageSendenUndAntwortArchivieren(hoerernummer, url);
            if (antwort.length > 0) {
                final DlsAntwort dlsAntwort = RestServiceClient.werteAntwortAus(antwort);
                LOGGER.trace("Abholen der Bestellung dauerte {} ms",
                        (System.nanoTime() - startBook) / 1_000_000);
                if (dlsAntwort instanceof DlsBook) {
                    return Optional.of((DlsBook) dlsAntwort);
                } else if (dlsAntwort instanceof DlsFehlermeldung) {
                    final DlsBook dlsBook = new DlsBook();
                    dlsBook.dlsFehlermeldung = (DlsFehlermeldung) dlsAntwort;
                    return Optional.of(dlsBook);
                } else {
                    LOGGER.warn("");
                    return Optional.empty();
                }
            } else {
                LOGGER.warn("Antwort von blista DLS hat Länge 0");
                return Optional.empty();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    private byte[] abfrageSendenUndAntwortArchivieren(final String hoerernummer,
                                                      final URL url) throws IOException {
        final byte[] antwort = RestServiceClient.download(
                dlsRestConfig.getBibliothek(), dlsRestConfig.getBibkennwort(),
                url);
        if (LOGGER.isDebugEnabled()) {
            final String normalizedUrl = url.getPath().replaceAll("/", "_").substring(1);
            archiviere(hoerernummer, normalizedUrl, antwort);
        }
        return antwort;
    }

    @Async
    protected void archiviere(final String hoerernummer, final String name, final byte[] daten) {
        try {
            final String now = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            final Path path = Path.of(/* TODO Konfigration */"var/blista/dls")
                    .resolve(hoerernummer).resolve(now + "-" + name + ".xml");
            Files.createDirectories(path.getParent());
            Files.write(path, daten, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

}
