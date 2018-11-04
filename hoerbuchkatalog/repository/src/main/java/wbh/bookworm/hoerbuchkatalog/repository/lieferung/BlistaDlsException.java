/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

public class BlistaDlsException extends RuntimeException {

    public BlistaDlsException(final String message) {
        super(message);
    }

    public BlistaDlsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BlistaDlsException(final Throwable cause) {
        super(cause);
    }

}
