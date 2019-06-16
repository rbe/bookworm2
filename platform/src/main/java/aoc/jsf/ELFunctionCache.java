/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.jsf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ELFunctionCache<T, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ELFunctionCache.class);

    private final Function<T, R> valueFunction;

    private final Map<T, R> values;

    private final Map<T, Boolean> invalidatedValues;

    public ELFunctionCache(final Function<T, R> valueFunction) {
        Objects.requireNonNull(valueFunction);
        this.valueFunction = valueFunction;
        this.values = new ConcurrentHashMap<>();
        this.invalidatedValues = new ConcurrentHashMap<>();
    }

    public synchronized R update(final T parameter) {
        Objects.requireNonNull(parameter);
        LOGGER.trace("Updating value for '{}' through function", parameter);
        final R value = valueFunction.apply(parameter);
        if (null != value) {
            values.put(parameter, value);
        }
        invalidatedValues.put(parameter, false);
        LOGGER.trace("Value for {} is now '{}'", parameter, value);
        return value;
    }

    public synchronized R get(final T parameter) {
        Objects.requireNonNull(parameter);
        final Boolean valueIsInvalidated = invalidatedValues.get(parameter);
        final boolean updateCalledBefore = null != valueIsInvalidated;
        if (updateCalledBefore && !valueIsInvalidated) {
            final R value = values.get(parameter);
            LOGGER.trace("Returning cached value: '{}'", value);
            return value;
        } else {
            return update(parameter);
        }
    }

    public synchronized void invalidate(final T parameter) {
        LOGGER.trace("Invalidating value for parameter '{}'", parameter);
        invalidatedValues.remove(parameter);
        values.remove(parameter, null);
    }

    public synchronized void invalidateAll() {
        LOGGER.trace("Invalidating all cached values");
        values.keySet().forEach(k -> {
            values.remove(k);
            invalidatedValues.remove(k);
        });
    }

}
