/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import wbh.bookworm.hoerbuchkatalog.domain.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.Titelnummer;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
final class HoerbuchFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchFactory.class);

    private static final int[] COLUMN_POSITIONS = {
            0, 3,       // pos# 0, Sachgebiet, A
            4, 8,       // pos# 2, Titelnummer, 12467
            9, 87,      // pos# 4, Autor, Abaelard, Petrus
            88, 207,    // pos# 6, Titel, Die Leidensgeschichte und der Briefwechsel mit Heloisa.
            208, 327,   // pos# 8, Erzählungen.
            328, 727,   // pos#10,
            728, 747,   // pos#12, Heidelberg
            748, 779,   // pos#14, Lambert Schneider
            780, 783,   // pos#16, 1979
            784, 833,   // pos#18, Lisa Bistrick
            834, 884,   // pos#20, Manfred Spitzer
            885, 890,   // pos#22, 434,00
            891, 912,   // pos#24, WBH Münster
            913, 916,   // pos#26, 1995
            917, 999,   // pos#28, Märchen
            1000, 1005, // pos#30, 1
            1006, 1008, // pos#32, 0
            1009, 1016, // pos#34, 20001030
            1017, 0,    // pos#36, 01-0044470-1-9
    };

    Hoerbuch fromGesamtDat(final String line) {
        LOGGER.trace("Erzeuge Hörbuch aus Gesamt.dat: {}", line);
        String[] arr = new String[COLUMN_POSITIONS.length / 2];
        for (int cp = 0; cp < COLUMN_POSITIONS.length; cp += 2) {
            int from = COLUMN_POSITIONS[cp];
            int to = COLUMN_POSITIONS[cp + 1];
            String substring;
            if (line.length() >= from) {
                if (to > 0) {
                    substring = line.substring(from, to + 1).trim();
                } else {
                    substring = line.substring(from).trim();
                }
                arr[cp / 2] = substring;
            }
        }
        final Titelnummer titelnummer = new Titelnummer(arr[1]);
        AghNummer aghNummer = null;
        if (!arr[18].isEmpty()) {
            try {
                aghNummer = new AghNummer(arr[18]);
            } catch (Exception e) {
                LOGGER.warn("Hörbuch Titelnummer#{} hat eine ungültige AGH Nummer '{}'",
                        titelnummer, arr[18]);
            }
        }
        return new Hoerbuch(
                Sachgebiet.valueOf(arr[0]), titelnummer,
                arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9],
                arr[10], arr[11], arr[12], arr[13], arr[14], arr[15], arr[16], arr[17],
                aghNummer, false
        );
    }

}
