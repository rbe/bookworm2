/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

public final class DomainValueException extends RuntimeException {

    public DomainValueException(final String message) {
        super(message);
    }

    public DomainValueException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DomainValueException(final Throwable cause) {
        super(cause);
    }

}
