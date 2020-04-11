/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

public class ObjectStorageException extends RuntimeException {

    public ObjectStorageException() {
    }

    public ObjectStorageException(final String message) {
        super(message);
    }

    public ObjectStorageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ObjectStorageException(final Throwable cause) {
        super(cause);
    }

    public ObjectStorageException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
