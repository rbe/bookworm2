/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.jsf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

public class ELTimedValueCache<R> extends ELValueCache<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ELTimedValueCache.class);

    private final Duration maxAgeMilliseconds;

    private LocalDateTime lastUpdated;

    public ELTimedValueCache(final R initialValue, final Supplier<R> valueSupplier,
                             final int maxAgeMilliseconds) {
        super(initialValue, valueSupplier);
        this.maxAgeMilliseconds = Duration.ofMillis(maxAgeMilliseconds);
    }

    @Override
    public R get() {
        final LocalDateTime now = LocalDateTime.now();
        if (Duration.between(now, lastUpdated).compareTo(maxAgeMilliseconds) == 1) {
            LOGGER.trace("Timeout; lastUpdated={} maxAge={} now={}", lastUpdated, maxAgeMilliseconds, now);
            invalidate();
        }
        return super.get();
    }

    @Override
    public R update() {
        lastUpdated = LocalDateTime.now();
        return super.update();
    }

    public static void main(String[] args) {
        final Duration secs5 = Duration.of(5, ChronoUnit.SECONDS);
        LocalDateTime now = LocalDateTime.now();
        final LocalDateTime minus4 = now.minusSeconds(4);
        final LocalDateTime plus0 = now.plusSeconds(0);
        final LocalDateTime plus6 = now.plusSeconds(6);
        // Duration.between(minus/plus, now) always returns 1!
        final Duration between4 = Duration.between(now, minus4);
        final Duration between5 = Duration.between(now, plus0);
        final Duration between6 = Duration.between(now, plus6);
        System.out.printf("%s %d%n", between4, secs5.compareTo(between4));
        System.out.printf("%s %d%n", between5, secs5.compareTo(between5));
        System.out.printf("%s %d%n", between6, secs5.compareTo(between6));
    }

}
