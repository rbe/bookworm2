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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Blista DLS
 * Agiert als ein ACL für Bestellungen -> domain.BlistaDownloads/BlistaDownload
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
            /* TODO Archivieren? final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(downloadToFile(hoerernummer, "werke", url)));*/
            final byte[] antwort = RestServiceClient.download(
                    dlsRestConfig.getBibliothek(), dlsRestConfig.getBibkennwort(),
                    url);
            // TODO antwort.length == 0
            final DlsAntwort dlsAntwort = RestServiceClient.werteAntwortAus(antwort);
            if (dlsAntwort instanceof DlsWerke) {
                return Optional.of((DlsWerke) dlsAntwort);
            } else if (dlsAntwort instanceof DlsFehlermeldung) {
                final DlsWerke dlsWerke = new DlsWerke();
                dlsWerke.dlsFehlermeldung = (DlsFehlermeldung) dlsAntwort;
                return Optional.of(dlsWerke);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    public Optional<DlsBook> bestellungLaden(final String hoerernummer, final String aghNummer) {
        final long startBook = System.nanoTime();
        try {
            final URL url = new URL(String.format("%s/%s/%s", dlsRestConfig.getWerkeurl(),
                    hoerernummer, aghNummer));
            /* TODO Archivieren? final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(downloadToFile(hoerernummer, "bestellung-" + aghNummer, url)));*/
            final byte[] download = RestServiceClient.download(
                    dlsRestConfig.getBibliothek(), dlsRestConfig.getBibkennwort(),
                    url);
            final DlsAntwort dlsAntwort = RestServiceClient.werteAntwortAus(download);
            LOGGER.trace("{}: Abholen der Bestellung dauerte {} ms", Thread.currentThread().getName(),
                    (System.nanoTime() - startBook) / 1_000_000);
            if (dlsAntwort instanceof DlsBook) {
                return Optional.of((DlsBook) dlsAntwort);
            } else if (dlsAntwort instanceof DlsFehlermeldung) {
                final DlsBook dlsBook = new DlsBook();
                dlsBook.dlsFehlermeldung = (DlsFehlermeldung) dlsAntwort;
                return Optional.of(dlsBook);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

}
