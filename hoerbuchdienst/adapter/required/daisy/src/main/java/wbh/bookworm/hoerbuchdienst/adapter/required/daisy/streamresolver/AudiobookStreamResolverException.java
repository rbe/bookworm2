/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver;

public class AudiobookStreamResolverException extends RuntimeException {

    public AudiobookStreamResolverException(final String message) {
        super(message);
    }

    public AudiobookStreamResolverException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
