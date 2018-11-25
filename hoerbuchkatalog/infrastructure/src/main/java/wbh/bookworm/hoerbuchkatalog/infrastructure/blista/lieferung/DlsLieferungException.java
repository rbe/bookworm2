/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung;

public class DlsLieferungException extends RuntimeException {

    public DlsLieferungException(final String message) {
        super(message);
    }

    public DlsLieferungException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DlsLieferungException(final Throwable cause) {
        super(cause);
    }

}
