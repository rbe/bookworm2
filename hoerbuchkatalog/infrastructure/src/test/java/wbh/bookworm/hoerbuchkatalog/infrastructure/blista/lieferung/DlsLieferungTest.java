/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest(classes = {LieferungTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class DlsLieferungTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlsLieferungTest.class);

    private final DlsLieferungConfig dlsLieferungConfig;

    @Autowired
    DlsLieferungTest(final DlsLieferungConfig dlsLieferungConfig) {
        this.dlsLieferungConfig = dlsLieferungConfig;
    }

    @Test
    void shouldFehlermeldungAuswerten() throws IOException {
        final InputStream inputStream = DlsLieferungTest.class
                .getResourceAsStream("/blista-xml/fehlermeldung.xml");
        final DlsLieferung.DlsAntwort dlsAntwort = new DlsLieferung(dlsLieferungConfig)
                .werteAntwortAus(inputStream.readAllBytes());
    }

/*
    @Test
    void shouldDownloadWerke() {
        final Hoerernummer hoerernummer = new Hoerernummer("7110");
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
    void shouldDownloadBestellung() {
        final Hoerernummer hoerernummer = new Hoerernummer("7110");
        final AghNummer aghNummer = new AghNummer("1-0081537-1-0");
        final DownloadsRepository.DlsBestellung bestellungLaden = new DownloadsRepository()
                .bestellungLaden(hoerernummer, aghNummer)
                .orElseThrow();
        LOGGER.info("Hörer {} hat AGH Nummer {} am {} bestellt",
                hoerernummer, aghNummer, bestellungLaden.book.Bestelldatum);
    }
*/

}
