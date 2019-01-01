/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.jsf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ELValueCacheGroup {

    private final List<InvalidatableValue<?>> elValueCaches;

    public ELValueCacheGroup() {
        this.elValueCaches = new LinkedList<>();
    }

    public ELValueCacheGroup(final InvalidatableValue<?>... elValueCaches) {
        this();
        register(elValueCaches);
    }

    public void register(final InvalidatableValue<?>... elValueCaches) {
        this.elValueCaches.addAll(Arrays.asList(elValueCaches));
    }

    public void unregister(final InvalidatableValue<?>... elValueCaches) {
        this.elValueCaches.removeAll(Arrays.asList(elValueCaches));
    }

    public void updateAll() {
        elValueCaches.forEach(InvalidatableValue::update);
    }

    public void invalidateAll() {
        elValueCaches.forEach(InvalidatableValue::invalidate);
    }

}
