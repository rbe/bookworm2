/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

public final class AudiobookShardingRepositoryException extends RuntimeException {

    public AudiobookShardingRepositoryException(final String message) {
        super(message);
    }

    public AudiobookShardingRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
