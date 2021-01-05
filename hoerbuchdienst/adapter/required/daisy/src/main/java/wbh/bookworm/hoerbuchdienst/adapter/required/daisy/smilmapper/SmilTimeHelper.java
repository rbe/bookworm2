/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import java.time.Duration;

final class SmilTimeHelper {

    private SmilTimeHelper() {
        throw new AssertionError();
    }

    static Duration parseClipNpt(final String clip) {
        if (null != clip && !clip.isBlank()) {
            return Duration.parse(String.format("PT%sS", clip.substring(4, clip.length() - 1)));
        } else {
            return Duration.ZERO;
        }
    }

    static Duration parseDuration(final String content) {
        if (null != content && !content.isBlank()) {
            return Duration.parse(String.format("PT%sH%sM%sS",
                    content.substring(0, 2), content.substring(3, 5), content.substring(6)));
        } else {
            return Duration.ZERO;
        }
    }

}
