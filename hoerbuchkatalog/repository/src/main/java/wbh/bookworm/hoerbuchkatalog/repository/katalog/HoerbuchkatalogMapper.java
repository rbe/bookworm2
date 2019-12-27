/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.mikrokosmos.io.dataformat.ColumnPositionLineFileParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Component
final class HoerbuchkatalogMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogMapper.class);

    private static final int[] COLUMN_POSITIONS = {
            /* arr# 0: Sachgebiet */0,
            /* arr# 1: Titelnummer */4,
            /* arr# 2: Autor */9,
            /* arr# 3: Titel */88,
            /* arr# 4: Erzählungen. */208,
            /* arr# 5: ? */328,
            /* arr# 6: Ort */728,
            /* arr# 7: Lambert Schneider */748,
            /* arr# 8: Jahr */780,
            /* arr# 9: Lisa Bistrick */784,
            /* arr#10: Manfred Spitzer */834,
            /* arr#11: 434,00 */885,
            /* arr#12: WBH Münster */891,
            /* arr#13: 1995 */913,
            /* arr#14: Märchen */917,
            /* arr#15: 1 */1000,
            /* arr#16: 0 */1004,
            /* arr#17: 20001030 */1009,
            /* arr#18: AGH Nummer */1017,
    };

    private final ColumnPositionLineFileParser columnPositionDatParser;

    public HoerbuchkatalogMapper() {
        columnPositionDatParser = new ColumnPositionLineFileParser(COLUMN_POSITIONS);
    }

    Set<Hoerbuch> importiere(final Path gesamtDat, final Charset charset) {
        Objects.requireNonNull(gesamtDat);
        LOGGER.info("Importiere Hörbücher aus '{}' mit Zeichensatz {}",
                gesamtDat, charset);
        try {
            /*
            final ColumnPositionLineFileParser columnPositionDatParser =
                    new ColumnPositionLineFileParser(COLUMN_POSITIONS);
            columnPositionDatParser.parse(gesamtDat, StandardCharsets.ISO_8859_1, 40_000,
                    s -> s.length == 19, this::ausGesamtDatEintrag);
            */
            final Set<Hoerbuch> hoerbuecher = Files.readAllLines(gesamtDat, charset)
                    .parallelStream()
                    .map(String::trim)
                    .filter(not(String::isBlank))
                    .map(this::ausGesamtDatEintrag)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            LOGGER.info("Insgesamt {} Hörbücher aus '{}' mit Zeichensatz {} importiert",
                    hoerbuecher.size(), gesamtDat, charset);
            return hoerbuecher;
        } catch (IOException e) {
            throw new HoerbuchkatalogArchivException(e);
        }
//  TODO      } else {
//            LOGGER.warn("Keinen Hörbuchkatalog im Archiv gefunden");
//            return Collections.emptySet();
//        }
    }

    private Hoerbuch ausGesamtDatEintrag(final String zeile) {
        try {
            final String[] arr = columnPositionDatParser.parseLine(zeile);
            final Titelnummer titelnummer = new Titelnummer(arr[1]);
            final AghNummer aghNummer = aghNummer(arr[18], titelnummer);
            return new Hoerbuch(Sachgebiet.valueOf(arr[0]), titelnummer,
                    arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9],
                    arr[10], arr[11], arr[12], arr[13], arr[14], arr[15], arr[16], arr[17],
                    aghNummer, false);
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    private AghNummer aghNummer(final String s, final Titelnummer titelnummer) {
        AghNummer aghNummer = null;
        if (!s.isEmpty()) {
            try {
                aghNummer = new AghNummer(s);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Hörbuch {} hat eine ungültige AGH Nummer '{}'",
                        titelnummer, s);
            }
        }
        return aghNummer;
    }

}
