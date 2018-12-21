/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

public final class SftpClientException extends RuntimeException {

    public SftpClientException(final String message) {
        super(message);
    }

    public SftpClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SftpClientException(final Throwable cause) {
        super(cause);
    }

    protected SftpClientException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
