/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.jsf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public class ELValueCache<R> implements InvalidatableValue<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ELValueCache.class);

    private final Supplier<R> valueSupplier;

    private volatile R value;

    private volatile boolean invalidated;

    public ELValueCache(final R initialValue, final Supplier<R> valueSupplier) {
        this.value = initialValue;
        Objects.requireNonNull(valueSupplier);
        this.valueSupplier = valueSupplier;
        this.invalidated = true;
    }

    @Override
    public synchronized R update() {
        LOGGER.trace("Updating value through supplier {}", valueSupplier);
        value = valueSupplier.get();
        LOGGER.debug("Value updated to '{}'", value);
        invalidated = false;
        return value;
    }

    @Override
    public synchronized R get() {
        if (!invalidated) {
            LOGGER.trace("Returning cached value: {}", value);
            return value;
        } else {
            return update();
        }
    }

    @Override
    public synchronized void invalidate() {
        LOGGER.trace("Invalidating {} for {}", this, valueSupplier);
        invalidated = true;
        value = null;
    }

}
