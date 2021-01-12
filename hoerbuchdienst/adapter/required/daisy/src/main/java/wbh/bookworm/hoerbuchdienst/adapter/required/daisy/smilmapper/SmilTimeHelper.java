/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SmilTimeHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmilTimeHelper.class);

    private SmilTimeHelper() {
        throw new AssertionError();
    }

    static Optional<Duration> parseClipNpt(final String clip) {
        if (null != clip && !clip.isBlank()) {
            try {
                return Optional.of(Duration.parse(String.format("PT%sS", clip.substring(4, clip.length() - 1))));
            } catch (DateTimeParseException e) {
                LOGGER.warn("Kann {} nicht parsen", clip);
            }
        }
        return Optional.empty();
    }

    static Optional<Duration> parseDuration(final String content) {
        if (null != content && !content.isBlank()) {
            try {
                return Optional.of(Duration.parse(String.format("PT%sH%sM%sS",
                        content.substring(0, 2), content.substring(3, 5), content.substring(6))));
            } catch (DateTimeParseException e) {
                LOGGER.warn("Kann {} nicht parsen", content);
            }
        }
        return Optional.empty();
    }

}
