/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.tools.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class ParseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseHelper.class);

    private ParseHelper() {
        throw new AssertionError();
    }

    public static LocalDate parseDate(final String hoerernummer,
                                      final String datum, final String str) {
        try {
            return null != str && !str.isBlank() && !"0".equals(str)
                    ? LocalDate.parse(str, DateTimeFormatter.BASIC_ISO_DATE)
                    : null;
        } catch (DateTimeParseException e) {
            try {
                final LocalDate yyyyddMM = LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyddMM"));
                LOGGER.warn("Hörer {} {} hat falsches Format: {}, korrigiert zu {}",
                        hoerernummer, datum, str, yyyyddMM);
                return yyyyddMM;
            } catch (DateTimeParseException e2) {
                LOGGER.warn("Hörer {} {} hat falsches Format: {}, Korrektur nicht möglich",
                        hoerernummer, datum, str);
                return null;
            }
        }
    }

    public static Integer parseInt(final String str) {
        return null != str && !str.isBlank()
                ? Integer.parseInt(str)
                : 0;
    }

}
