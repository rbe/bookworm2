/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.api;

public abstract class HoerbuchException extends BusinessException {

    public HoerbuchException(final String message) {
        super(message);
    }

    public HoerbuchException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
