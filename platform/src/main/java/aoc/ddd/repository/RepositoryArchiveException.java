/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

public class RepositoryArchiveException extends Exception {

    public RepositoryArchiveException(final String message) {
        super(message);
    }

    public RepositoryArchiveException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RepositoryArchiveException(final Throwable cause) {
        super(cause);
    }

}
