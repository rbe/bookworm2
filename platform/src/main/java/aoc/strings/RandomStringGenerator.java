/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.strings;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class RandomStringGenerator {

    private static final SecureRandom random = new SecureRandom();

    private RandomStringGenerator() {
        throw new AssertionError();
    }

    public static String next() {
        return new BigInteger(128, random).toString(32);
    }

}
