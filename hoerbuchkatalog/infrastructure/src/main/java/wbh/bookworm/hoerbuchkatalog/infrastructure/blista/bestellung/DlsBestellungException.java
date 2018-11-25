/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

public class DlsBestellungException extends RuntimeException {

    public DlsBestellungException(final String message) {
        super(message);
    }

    public DlsBestellungException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DlsBestellungException(final Throwable cause) {
        super(cause);
    }

}
