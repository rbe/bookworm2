/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

public class Mp3RepositoryException extends RuntimeException {

    public Mp3RepositoryException() {
    }

    public Mp3RepositoryException(final String message) {
        super(message);
    }

    public Mp3RepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public Mp3RepositoryException(final Throwable cause) {
        super(cause);
    }

    public Mp3RepositoryException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
