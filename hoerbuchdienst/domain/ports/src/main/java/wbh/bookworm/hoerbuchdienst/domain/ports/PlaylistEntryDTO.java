/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public final class PlaylistEntryDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private static final Double[] NO_CLIPS = {};

    private final String title;

    private final String ident;

    private final Double[] clips;

    public PlaylistEntryDTO(final String title, final String ident) {
        this.title = title;
        this.ident = ident;
        this.clips = NO_CLIPS;
    }

    public PlaylistEntryDTO(final String title, final String ident, final Double[] clips) {
        this.title = title;
        this.ident = ident;
        this.clips = clips;
    }

    public String getTitle() {
        return title;
    }

    public String getIdent() {
        return ident;
    }

    public Double[] getClips() {
        return clips;
    }

    public double getMilliseconds() {
        return Arrays.stream(clips)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public Duration getDuration() {
        return Duration.of((long) getMilliseconds(), ChronoUnit.MILLIS);
    }

    @Override
    public String toString() {
        return String.format("PlaylistEntryDTO{title='%s', ident='%s', duration=%s}", title, ident, getDuration());
    }

}
