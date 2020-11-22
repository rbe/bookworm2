/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsRestConfig;

@SpringBootTest(classes = {DlsLieferungTestAppConfig.class})
@SpringBootConfiguration
@ExtendWith(SpringExtension.class)
@Disabled
class DlsLieferungTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlsLieferungTest.class);

    private final DlsRestConfig dlsRestConfig;

    @Autowired
    DlsLieferungTest(final DlsRestConfig dlsRestConfig) {
        this.dlsRestConfig = dlsRestConfig;
    }

    @Test
    void shouldPrintConfig() {
        System.out.println(dlsRestConfig);
    }

    /*
    @Test
    @Disabled("Werte ändern sich")
    void shouldDownloadWerke() {
        final Hoerernummer hoerernummer = new Hoerernummer("80170");
        final DownloadsRepository.DlsWerke dlsWerke = new DownloadsRepository()
                .alleWerkeLaden(hoerernummer)
                .orElseThrow();
        if (dlsWerke.hatFehler()) {
            LOGGER.info("{}", dlsWerke.dlsFehlermeldung.fehler.fehlermeldung);
        } else {
            LOGGER.info("Hörer {} hat {} Werke bestellt", hoerernummer, dlsWerke.books.size());
        }
    }
    */

    /*
    @Test
    @Disabled("Werte ändern sich")
    void shouldDownloadBestellung() {
        final Hoerernummer hoerernummer = new Hoerernummer("7110");
        final AghNummer aghNummer = new AghNummer("1-0081537-1-0");
        final DownloadsRepository.DlsBook bestellungLaden = new DownloadsRepository()
                .bestellungLaden(hoerernummer, aghNummer)
                .orElseThrow();
        LOGGER.info("Hörer {} hat AGH Nummer {} am {} bestellt",
                hoerernummer, aghNummer, bestellungLaden.book.Bestelldatum);
    }
    */

}
