/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

public final class ElasticsearchClientException extends RuntimeException {

    public ElasticsearchClientException() {
    }

    public ElasticsearchClientException(final String message) {
        super(message);
    }

    public ElasticsearchClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ElasticsearchClientException(final Throwable cause) {
        super(cause);
    }

    public ElasticsearchClientException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
