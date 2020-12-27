/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.as400;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class Datenformat {

    private Datenformat() {
        throw new AssertionError();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Datenformat.class);

    public static LocalDate localDateOf(final String hoerernummer,
                                        final String datum, final String str) {
        try {
            return aoc.mikrokosmos.io.dataformat.ParseHelper.parseDate(str);
        } catch (DateTimeParseException e) {
            try {
                final LocalDate yyyyddMM = LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyddMM"));
                LOGGER.warn("Hörer '{}': '{}' hat falsches Format: '{}', korrigiert zu '{}'",
                        hoerernummer, datum, str, yyyyddMM);
                return yyyyddMM;
            } catch (DateTimeParseException e2) {
                LOGGER.warn("Hörer '{}': '{}' hat falsches Format: '{}', Korrektur nicht möglich",
                        hoerernummer, datum, str);
                return null;
            }
        }
    }

}
