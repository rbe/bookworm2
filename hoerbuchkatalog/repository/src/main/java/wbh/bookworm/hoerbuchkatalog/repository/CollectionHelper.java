/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

final class CollectionHelper {

    private CollectionHelper() {
        throw new AssertionError();
    }

    static <E> Collection<E> intersection(final Collection<E> a, final Collection<E> b) {
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

    public static void main(String[] args) {
        List<String> a = Arrays.asList("a", "b", "c");
        List<String> b = Arrays.asList("x", "y", "z", "b");
        intersection(a, b).forEach(System.out::println);
    }

}
