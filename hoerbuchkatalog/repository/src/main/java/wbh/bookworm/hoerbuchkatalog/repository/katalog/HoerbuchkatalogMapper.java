/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Component
final class HoerbuchkatalogMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogMapper.class);

    private final HoerbuchkatalogConfig hoerbuchkatalogConfig;

    private final HoerbuchkatalogArchiv hoerbuchkatalogArchiv;

    @Autowired
    HoerbuchkatalogMapper(final HoerbuchkatalogConfig hoerbuchkatalogConfig,
                          final HoerbuchkatalogArchiv hoerbuchkatalogArchiv) {
        this.hoerbuchkatalogConfig = hoerbuchkatalogConfig;
        this.hoerbuchkatalogArchiv = hoerbuchkatalogArchiv;
    }

    void aktualisiereArchiv() {
        final Path hoerbuchkatalogDirectory = hoerbuchkatalogConfig.getHoerbuchkatalogDirectory();
        final Path gesamtDat = hoerbuchkatalogDirectory.resolve(hoerbuchkatalogConfig.getWbhGesamtdatFilename());
        if (Files.exists(gesamtDat)) {
            LOGGER.info("Aktualisiere Hörbuchkatalog aus Archiv '{}'", gesamtDat);
            hoerbuchkatalogArchiv.archiviereKatalog(gesamtDat);
        } else {
            LOGGER.info("'{}' nicht gefunden", gesamtDat);
        }
    }

    Set<Hoerbuch> importiereAusArchiv(final Path gesamtDat) {
        Objects.requireNonNull(gesamtDat);
        LOGGER.info("Importiere Hörbücher aus Archiv '{}'", gesamtDat);
        try {
            final Set<Hoerbuch> hoerbuecher = gesamtDatEinlesen(gesamtDat);
            LOGGER.info("Insgesamt {} Hörbücher aus Archiv '{}' importiert", hoerbuecher.size(), gesamtDat);
            return hoerbuecher;
        } catch (IOException e) {
            throw new HoerbuchkatalogArchivException(e);
        }
//        } else {
//            LOGGER.warn("Keinen Hörbuchkatalog im Archiv gefunden");
//            return Collections.emptySet();
//        }
    }

    private Set<Hoerbuch> gesamtDatEinlesen(final Path gesamtDat) throws IOException {
        try (final BufferedReader reader = Files.newBufferedReader(
                gesamtDat, hoerbuchkatalogConfig.getWbhGesamtdatCharset())) {
            final Set<Hoerbuch> hoerbuecher = new TreeSet<>();
            String line;
            while (null != (line = reader.readLine()) && line.trim().length() > 1) {
                final Hoerbuch hoerbuch = ausGesamtDatEintrag(line);
                hoerbuecher.add(hoerbuch);
            }
            return hoerbuecher;
        }
    }

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

    private Hoerbuch ausGesamtDatEintrag(final String zeile) {
        LOGGER.trace("Erzeuge Hörbuch aus Gesamt.dat: {}", zeile);
        final String[] arr = new String[COLUMN_POSITIONS.length / 2];
        for (int cp = 0; cp < COLUMN_POSITIONS.length; cp += 2) {
            int from = COLUMN_POSITIONS[cp];
            int to = COLUMN_POSITIONS[cp + 1];
            String substring;
            if (zeile.length() >= from) {
                if (to > 0) {
                    substring = zeile.substring(from, to + 1).trim();
                } else {
                    substring = zeile.substring(from).trim();
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
