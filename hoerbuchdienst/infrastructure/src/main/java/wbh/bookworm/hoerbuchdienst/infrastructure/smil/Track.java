/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.infrastructure.smil;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class Track implements Serializable {

    private static final long serialVersionUID = -1L;

    private String title;

    private String source;

    private Duration timeInThisSmil;

    private Duration totalTimeElapsed;

    private List<Audioclip> audioclips;

    public Track() {
        this.audioclips = new ArrayList<>();
    }

    public Track(final String title, final String source,
                 final Duration timeInThisSmil, final Duration totalTimeElapsed,
                 final List<Audioclip> audioclips) {
        this.title = title;
        this.source = source;
        this.timeInThisSmil = timeInThisSmil;
        this.totalTimeElapsed = totalTimeElapsed;
        this.audioclips = List.copyOf(audioclips);
    }

    public String getTitle() {
        return title;
    }

    void setTitle(final String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    void setSource(final String source) {
        this.source = source;
    }

    public Duration getTimeInThisSmil() {
        return timeInThisSmil;
    }

    void setTimeInThisSmil(final Duration timeInThisSmil) {
        this.timeInThisSmil = timeInThisSmil;
    }

    public Duration getTotalTimeElapsed() {
        return totalTimeElapsed;
    }

    void setTotalTimeElapsed(final Duration totalTimeElapsed) {
        this.totalTimeElapsed = totalTimeElapsed;
    }

    public Audioclip[] getAudioclips() {
        return audioclips.toArray(Audioclip[]::new);
    }

    void setAudioclips(final List<Audioclip> audioclips) {
        this.audioclips = audioclips;
    }

    void add(final Audioclip audioclip) {
        this.audioclips.add(audioclip);
    }

    @Override
    public String toString() {
        return String.format("Title{title='%s', source='%s', timeInThisSmil=%s, totalTimeElapsed=%s, sequence=%s}",
                title, source, timeInThisSmil, totalTimeElapsed, audioclips);
    }

}
