/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

public class DomainRepositoryException extends RuntimeException {

    public DomainRepositoryException() {
    }

    public DomainRepositoryException(final String message) {
        super(message);
    }

    public DomainRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DomainRepositoryException(final Throwable cause) {
        super(cause);
    }

    public DomainRepositoryException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
