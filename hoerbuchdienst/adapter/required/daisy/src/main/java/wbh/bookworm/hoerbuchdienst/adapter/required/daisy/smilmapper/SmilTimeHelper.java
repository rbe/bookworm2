/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SmilTimeHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmilTimeHelper.class);

    private SmilTimeHelper() {
        throw new AssertionError();
    }

    private static long countColons(final String str) {
        return str.chars()
                .filter(i -> Objects.equals((int) ':', i))
                .count();
    }

    private static Stream<Integer[]> strsBetweenColons(final String str) {
        final List<Integer[]> list = new ArrayList<>();
        int lastPos = 0;
        int pos = str.indexOf(':');
        if (-1 < pos) {
            while (-1 < pos) {
                list.add(new Integer[]{lastPos, pos});
                lastPos = pos + 1;
                pos = str.indexOf(':', pos + 1);
            }
            list.add(new Integer[]{lastPos, str.length()});
        } else {
            list.add(new Integer[]{0, str.length()});
        }
        return list.stream();
    }

    static Optional<Duration> parse(final String str) {
        final String s;
        if (str.endsWith("s")) {
            s = str.substring(0, str.length() - 1);
        } else {
            s = str;
        }
        final Object[] strs = strsBetweenColons(s)
                .map(a -> s.substring(a[0], a[1]))
                .toArray(Object[]::new);
        try {
            return switch ((int) countColons(str)) {
                case 0 -> Optional.of(Duration.parse(String.format("PT%sS", strs)));
                case 1 -> Optional.of(Duration.parse(String.format("PT%sM%sS", strs)));
                case 2 -> Optional.of(Duration.parse(String.format("PT%sH%sM%sS", strs)));
                default -> throw new IllegalStateException("Unexpected value: " + (int) countColons(str));
            };
        } catch (DateTimeParseException e) {
            LOGGER.warn("Kann Dauer '{}' nicht parsen", str);
            return Optional.empty();
        }
    }

}
