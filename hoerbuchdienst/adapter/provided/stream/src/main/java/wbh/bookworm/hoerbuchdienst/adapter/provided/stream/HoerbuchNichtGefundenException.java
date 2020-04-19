/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;

public class HoerbuchNichtGefundenException extends BusinessException {

    public HoerbuchNichtGefundenException() {
    }

    public HoerbuchNichtGefundenException(final String message) {
        super(message);
    }

    public HoerbuchNichtGefundenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HoerbuchNichtGefundenException(final Throwable cause) {
        super(cause);
    }

    public HoerbuchNichtGefundenException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
