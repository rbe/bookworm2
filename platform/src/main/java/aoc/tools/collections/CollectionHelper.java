/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.tools.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class CollectionHelper {

    private CollectionHelper() {
        throw new AssertionError();
    }

    public static <E> Collection<E> intersection(final Collection<E> a, final Collection<E> b) {
        final BiFunction<Collection<E>, Collection<E>, Collection<E>> intersector =
                (smallest, biggest) ->
                        smallest.parallelStream()
                                .filter(biggest::contains)
                                .collect(Collectors.toList());
        if (null == a || a.isEmpty() || null == b || b.isEmpty()) {
            return Collections.emptyList();
        } else {
            return a.size() < b.size()
                    ? intersector.apply(a, b)
                    : intersector.apply(b, a);
        }
    }

}
