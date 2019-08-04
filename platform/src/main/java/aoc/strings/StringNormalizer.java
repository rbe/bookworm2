/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.strings;

import java.security.SecureRandom;

public final class StringNormalizer {

    private static final SecureRandom random = new SecureRandom();

    private StringNormalizer() {
        throw new AssertionError();
    }

    public static String normalize(String string) {
        return java.text.Normalizer.normalize(string, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

}
