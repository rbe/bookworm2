/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.datatransfer;

import org.slf4j.Logger;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public final class Timer {

    private Timer() {
        throw new AssertionError();
    }

    public static <T extends List> T call(final Logger logger, final Supplier<T> fun) {
        final LocalDateTime start = LocalDateTime.now();
        final T result = fun.get();
        if (logger.isDebugEnabled()) {
            logger.debug("Producing {} list entries took {}",
                    null != result
                            ? NumberFormat.getInstance(Locale.GERMANY).format(result.size())
                            : "(unknown)",
                    Duration.between(start, LocalDateTime.now()));
        }
        return result;
    }

}
