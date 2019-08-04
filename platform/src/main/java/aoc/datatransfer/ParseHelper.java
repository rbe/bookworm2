/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class ParseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseHelper.class);

    private ParseHelper() {
        throw new AssertionError();
    }

    public static LocalDate parseDate(final String str) {
        Objects.requireNonNull(str);
        final String datum = str.replaceAll("-", "")
                .replaceAll("\\.", "");
        return !str.isBlank() && str.length() >= 8
                ? LocalDate.parse(datum, DateTimeFormatter.BASIC_ISO_DATE)
                : null;
    }

    public static Integer parseInt(final String str) {
        return null != str && !str.isBlank()
                ? Integer.parseInt(str)
                : 0;
    }

}
