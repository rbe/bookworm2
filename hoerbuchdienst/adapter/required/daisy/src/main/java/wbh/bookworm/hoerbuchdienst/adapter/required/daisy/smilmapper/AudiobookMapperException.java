/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

public class AudiobookMapperException extends RuntimeException {

    public AudiobookMapperException() {
    }

    public AudiobookMapperException(final String message) {
        super(message);
    }

    public AudiobookMapperException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AudiobookMapperException(final Throwable cause) {
        super(cause);
    }

    public AudiobookMapperException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
