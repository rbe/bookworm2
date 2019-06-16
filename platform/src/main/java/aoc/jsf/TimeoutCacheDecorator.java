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

public final class TimeoutCacheDecorator<R> implements InvalidatableValue<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutCacheDecorator.class);

    private final InvalidatableValue<R> delegate;

    private final Duration maxAgeMilliseconds;

    private volatile LocalDateTime lastUpdated;

    public TimeoutCacheDecorator(final InvalidatableValue<R> delegate,
                                 final long maxAgeMilliseconds) {
        this.delegate = delegate;
        this.maxAgeMilliseconds = Duration.ofMillis(maxAgeMilliseconds);
        lastUpdated = LocalDateTime.now().minusSeconds(this.maxAgeMilliseconds.getSeconds());
    }

    @Override
    public R get() {
        return update();
    }

    @Override
    public synchronized R update() {
        if (isUpdateTimeoutReached()) {
            LOGGER.trace("Invalidating cache for {}", delegate);
            delegate.invalidate();
            lastUpdated = LocalDateTime.now();
            return delegate.update();
        } else {
            return delegate.get();
        }
    }

    @Override
    public synchronized void invalidate() {
        delegate.invalidate();
    }

    private synchronized boolean isUpdateTimeoutReached() {
        final Duration duration = Duration.between(lastUpdated, LocalDateTime.now());
        final boolean b = duration.compareTo(maxAgeMilliseconds) > 0;
        LOGGER.trace("{} duration={} maxAgeReached={}", delegate, duration, b);
        return b;
    }

    /*
    public static void main(String[] args) {
        final Duration secs5 = Duration.of(5, ChronoUnit.SECONDS);
        LocalDateTime now = LocalDateTime.now();
        final LocalDateTime minus4 = now.minusSeconds(4);
        final LocalDateTime plus0 = now.plusSeconds(0);
        final LocalDateTime plus6 = now.plusSeconds(6);
        // Duration.between(minus/plus, now) always returns 1!
        final Duration between4 = Duration.between(now, minus4);
        System.out.printf("-4s: %s compareTo=%d%n", between4, secs5.compareTo(between4));
        final Duration between5 = Duration.between(now, plus0);
        System.out.printf(" 0s: %s compareTo=%d%n", between5, secs5.compareTo(between5));
        final Duration between6 = Duration.between(now, plus6);
        System.out.printf("+6s: %s compareTo=%d%n", between6, secs5.compareTo(between6));
    }
    */

}
