/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.jsf;

import java.util.LinkedList;
import java.util.List;

public class ELCacheGroup {

    private final List<ELValueCache<?>> elValueCaches;

    public ELCacheGroup() {
        this.elValueCaches = new LinkedList<>();
    }

    public void register(final ELValueCache<?> elValueCache) {
        elValueCaches.add(elValueCache);
    }

    public void unregister(final ELValueCache<?> elValueCache) {
        elValueCaches.remove(elValueCache);
    }

    public void updateAll() {
        elValueCaches.forEach(ELValueCache::update);
    }

    public void invalidateAll() {
        elValueCaches.forEach(ELValueCache::invalidate);
    }

}
