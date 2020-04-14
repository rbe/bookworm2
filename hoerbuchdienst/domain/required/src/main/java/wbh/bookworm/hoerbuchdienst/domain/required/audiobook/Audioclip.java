/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobook;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

public final class Audioclip implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String filename;

    private final Duration begin;

    private final Duration endExclusive;

    public Audioclip(final String filename, final Duration begin, final Duration endExclusive) {
        this.filename = filename;
        this.begin = begin;
        this.endExclusive = endExclusive;
    }

    public String getFilename() {
        return filename;
    }

    public Duration getBegin() {
        return begin;
    }

    public Duration getEndExclusive() {
        return endExclusive;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Audioclip audioclip = (Audioclip) o;
        return filename.equals(audioclip.filename) &&
                begin.equals(audioclip.begin) &&
                endExclusive.equals(audioclip.endExclusive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, begin, endExclusive);
    }

    @Override
    public String toString() {
        return String.format("Audioclip{filename='%s', begin=%s, endExclusive=%s}", filename, begin, endExclusive);
    }

}
