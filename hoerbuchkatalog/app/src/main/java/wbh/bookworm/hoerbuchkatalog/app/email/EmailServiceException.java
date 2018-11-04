/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

public class EmailServiceException extends RuntimeException {

    public EmailServiceException() {
    }

    public EmailServiceException(final String message) {
        super(message);
    }

    public EmailServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EmailServiceException(final Throwable cause) {
        super(cause);
    }

    public EmailServiceException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
