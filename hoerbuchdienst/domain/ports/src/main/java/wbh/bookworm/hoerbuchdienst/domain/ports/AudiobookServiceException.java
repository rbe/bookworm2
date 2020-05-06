/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

public class AudiobookServiceException extends RuntimeException {

    public AudiobookServiceException() {
    }

    public AudiobookServiceException(final String message) {
        super(message);
    }

    public AudiobookServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AudiobookServiceException(final Throwable cause) {
        super(cause);
    }

    public AudiobookServiceException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
