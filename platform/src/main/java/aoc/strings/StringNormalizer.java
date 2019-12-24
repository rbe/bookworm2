/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.strings;

import java.text.Normalizer;

public final class StringNormalizer {

    //private static final String NOT_ALLOWED_CHARACTERS = "[^a-zA-ZäÄöÖüÜß0-9 ,-]";
    //private static final String NOT_ALLOWED_CHARACTERS = "[^\\p{ASCII}]";
    //private static final String NOT_ALLOWED_CHARACTERS = "^[\\u0000-\\u007F]";
    private static final String ALLOWED_CHARACTERS = "[\\u002D|\\u0030-\\u0039|\\u0041-\\u005A|\\u0061-\\u006A|\\u00C0-\\u00D6|\\u00D8-\\u00F6|\\u00F8-\\u00FF]";
    private static final String NOT_ALLOWED_CHARACTERS = "^[\\u002D|\\u0030-\\u0039|\\u0041-\\u005A|\\u0061-\\u006A|\\u00C0-\\u00D6|\\u00D8-\\u00F6|\\u00F8-\\u00FF]";

    private StringNormalizer() {
        throw new AssertionError();
    }

    // TODO Test normalize("ÜäöÄgläÖÜbÄüglüß");
    public static String normalize(final String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll(NOT_ALLOWED_CHARACTERS, "");
    }

    public static void main(String[] args) {
        //String stichwort = new String("ABAabdorján".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        String stichwort = "ü";
        System.out.println(normalize(stichwort));
    }

}
