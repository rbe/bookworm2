/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import java.math.BigInteger;
import java.security.SecureRandom;

final class RandomStringGenerator {

    private static final SecureRandom random = new SecureRandom();

    private RandomStringGenerator() {
        throw new AssertionError();
    }

    static String next() {
        return new BigInteger(128, random).toString(32);
    }

}
