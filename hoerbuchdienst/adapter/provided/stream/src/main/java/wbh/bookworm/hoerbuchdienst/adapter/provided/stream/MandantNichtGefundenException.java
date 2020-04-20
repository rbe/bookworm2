/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;

public class MandantNichtGefundenException extends BusinessException {

    public MandantNichtGefundenException() {
    }

    public MandantNichtGefundenException(final String message) {
        super(message);
    }

    public MandantNichtGefundenException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
