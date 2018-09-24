/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.tools;

import wbh.bookworm.platform.collections.CollectionHelper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionHelperTest {

    @Test
    void shouldFindIntersection() {
        List<String> a = Arrays.asList("a", "b", "c");
        List<String> b = Arrays.asList("x", "y", "z", "b");
        final Collection<String> intersection = CollectionHelper.intersection(a, b);
        assertEquals(Arrays.asList("b"), intersection);
    }

}
