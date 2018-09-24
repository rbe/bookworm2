/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

final class ImportFailedException extends Exception {

    ImportFailedException(final String message) {
        super(message);
    }

    ImportFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    ImportFailedException(final Throwable cause) {
        super(cause);
    }

}
