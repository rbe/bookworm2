/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobook;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public final class Audiobook implements Serializable {

    private static final long serialVersionUID = -1L;

    @JsonView(AudiobookViews.SearchIndex.class)
    private String titelnummer;

    /** master.smil/head/dc:identifier */
    @JsonView(AudiobookViews.Default.class)
    private String identifier;

    /**
     * z.B. "DAISY 2.02".
     */
    @JsonView(AudiobookViews.Default.class)
    private String format;

    /**
     * dc:publisher, z.B. "WBH"
     */
    @JsonView(AudiobookViews.SearchIndex.class)
    private String publisher;

    /**
     *
     */
    @JsonView(AudiobookViews.Default.class)
    private String date;

    /**
     *
     */
    @JsonView(AudiobookViews.Default.class)
    private String language;

    /**
     *
     */
    @JsonView(AudiobookViews.SearchIndex.class)
    private String isbn;

    /**
     *
     */
    @JsonView(AudiobookViews.SearchIndex.class)
    private String author;

    /**
     *
     */
    @JsonView(AudiobookViews.Default.class)
    private String setInfo;

    /**
     * ncc.html/head/meta narrator.
     */
    @JsonView(AudiobookViews.SearchIndex.class)
    private String narrator;

    /**
     * ncc.html/head/meta audioformat.
     */
    @JsonView(AudiobookViews.Default.class)
    private String audioformat;

    /**
     * ncc.html/head/meta compression.
     */
    @JsonView(AudiobookViews.Default.class)
    private String compression;

    /**
     * master.smil/head/dc:title
     */
    @JsonView(AudiobookViews.SearchIndex.class)
    private String title;

    /**
     * ncc:tocItems
     */
    @JsonView(AudiobookViews.Default.class)
    private int tocItems;

    /**
     * master.smil/head/ncc:timeInThisSmil.
     * Gesamte Länge des Hörbuchs.
     */
    @JsonView(AudiobookViews.Default.class)
    private Duration timeInThisSmil;

    /**
     * ncc:totalTime
     */
    @JsonView(AudiobookViews.Default.class)
    private Duration totalTime;

    /**
     * master.smil/body/ref
     */
    @JsonView(AudiobookViews.Default.class)
    private Audiotrack[] audiotracks;

    @JsonView(AudiobookViews.Default.class)
    private String sourceDate;

    @JsonView(AudiobookViews.SearchIndex.class)
    private String sourcePublisher;

    public void setTocItems(final String tocItems) {
        this.tocItems = Integer.parseInt(tocItems);
    }

    public Audiotrack[] getAudiotracks() {
        //return Arrays.copyOf(titles, titles.length);
        return audiotracks.clone();
    }

    public void setAudiotracks(final List<Audiotrack> audiotracks) {
        this.audiotracks = audiotracks.toArray(Audiotrack[]::new);
    }

}
