/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

@Component
final class BestellkartenMapper {

    private static final int[] COLUMN_LENGTHS = {
            /* BKHNR: HÖRER-NR */5,
            /* BKAIN: AUS-INDEX */3,
            /* BKAOP: OP */3,
            /* BKP1: BEST1 */7,
            /* BKP2: BEST2 */7,
            /* BKP3: BEST3 */7,
            /* BKP4: BEST4 */7,
            /* BKP5: BEST5 */7,
            /* BKP6: BEST6 */7,
            /* BKP7: BEST7 */7,
            /* BKP8: BEST8 */7,
            /* BKP9: BEST9 */7,
            /* BKP10: BEST10 */7,
            /* BKP11: BEST11 */7,
            /* BKP12: BEST12 */7,
            /* BKP13: BEST13 */7,
            /* BKP14: BEST14 */7,
            /* BKP15: BEST15 */7,
            /* BKP16: BEST16 */7,
            /* BKP17: BEST17 */7,
            /* BKP18: BEST18 */7,
            /* BKP19: BEST19 */7,
            /* BKP20: BEST20 */7,
            /* BKP21: BEST21 */7,
            /* ...: , */
            /* BKP393: BEST393 */7,
            /* BKP394: BEST394 */7,
            /* BKP395: BEST395 */7,
            /* BKP396: BEST396 */7,
            /* BKP397: BEST397 */7,
            /* BKP398: BEST398 */7,
            /* BKP399: BEST399 */7,
            /* BKP400: BEST400 */7,
            /* BKPDAT: LETZT.BEST.DATUM */8,
    };

    void leseAs400Datei(final Path bkstpDat, final Charset charset,
                               int expectedLineCount) {
    }

    public List<Bestellkarte> bestellkartenFuer(final Hoerernummer hoerernummer) {
        return null;
    }

}
