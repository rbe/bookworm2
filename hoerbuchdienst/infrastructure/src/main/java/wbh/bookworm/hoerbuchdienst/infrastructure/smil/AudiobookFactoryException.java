/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.infrastructure.smil;

public class AudiobookFactoryException extends RuntimeException {

    public AudiobookFactoryException() {
    }

    public AudiobookFactoryException(final String message) {
        super(message);
    }

    public AudiobookFactoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AudiobookFactoryException(final Throwable cause) {
        super(cause);
    }

    public AudiobookFactoryException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
