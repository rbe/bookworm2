/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.VerfuegbareDownloads;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryTestAppConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {RepositoryTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class DownloadsRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsRepositoryTest.class);

    private final DownloadsConfig downloadsConfig;

    @Autowired
    DownloadsRepositoryTest(final DownloadsConfig downloadsConfig) {
        this.downloadsConfig = downloadsConfig;
    }

/*
    @Test
    void shouldFehlermeldungAuswerten() {
        final InputStream inputStream = DownloadsRepository.class.getResourceAsStream("/dlsFehlermeldung.xml");
        final DownloadsRepository.DlsAntwort dlsAntwort = new DownloadsRepository()
                .werteAntwortAus(inputStream)
                .orElseThrow();
    }
*/

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

    @Test
    void shouldAlleLieferungen() {
        final VerfuegbareDownloads lieferungen =
                new DownloadsRepository(downloadsConfig).lieferungen(new Hoerernummer("80170"));
        LOGGER.debug("{}", lieferungen);
    }

}
