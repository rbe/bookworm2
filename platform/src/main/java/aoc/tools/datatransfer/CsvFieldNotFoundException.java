/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.tools.datatransfer;

public final class CsvFieldNotFoundException extends RuntimeException {

    public CsvFieldNotFoundException() {
        super();
    }

    public CsvFieldNotFoundException(final String message) {
        super(message);
    }

}
