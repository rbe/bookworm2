/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.jsf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public final class ELValueCache<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ELValueCache.class);

    private final Supplier<R> valueSupplier;

    private R value;

    private boolean invalidated;

    public ELValueCache(final R initialValue, final Supplier<R> valueSupplier) {
        this.value = initialValue;
        Objects.requireNonNull(valueSupplier);
        this.valueSupplier = valueSupplier;
        this.invalidated = true;
    }

    public R update() {
        LOGGER.trace("Updating value through supplier");
        value = valueSupplier.get();
        LOGGER.trace("Value is now '{}'", value);
        invalidated = false;
        return value;
    }

    public R get() {
        if (!invalidated) {
            LOGGER.trace("Returning cached value: {}", value);
            return value;
        } else {
            return update();
        }
    }

    public void invalidate() {
        LOGGER.trace("Invalidating");
        invalidated = true;
        value = null;
    }

}
