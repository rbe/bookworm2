/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.jsf;

public interface InvalidatableValue<R> {

    /**
     * Never call this method directly, use {@link #get()}.
     */
    R update();

    /**
     * Returns value set through {@link #update()} before as long as
     * {@link #invalidate()} was not called.
     */
    R get();

    /**
     * Invalidates and removes the value.
     */
    void invalidate();

}
