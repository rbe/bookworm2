/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.VerfuegbareDownloads;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@SpringBootTest(classes = {RepositoryTestAppConfig.class})
//@ExtendWith(SpringExtension.class)
class DownloadsRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsRepositoryTest.class);

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
                .werke(hoerernummer)
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
        final DownloadsRepository.DlsBestellung bestellung = new DownloadsRepository()
                .bestellung(hoerernummer, aghNummer)
                .orElseThrow();
        LOGGER.info("Hörer {} hat AGH Nummer {} am {} bestellt",
                hoerernummer, aghNummer, bestellung.book.Bestelldatum);
    }
*/

    @Test
    void shouldAlleLieferungen() {
        final VerfuegbareDownloads lieferungen =
                new DownloadsRepository().lieferungen(new Hoerernummer("80170"));
        LOGGER.debug("{}", lieferungen);
    }

}
