/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

public class BestellungException extends RuntimeException {

    public BestellungException() {
        super();
    }

    public BestellungException(final String message) {
        super(message);
    }

    public BestellungException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BestellungException(final Throwable cause) {
        super(cause);
    }

    protected BestellungException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
