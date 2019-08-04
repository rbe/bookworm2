/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.strings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RandomStringGeneratorTest {

    @Test
    void next() {
        for (int i = 0; i < 1_000; i++) {
            final String next = RandomStringGenerator.next();
            assertNotNull(next);
        }
    }

}
