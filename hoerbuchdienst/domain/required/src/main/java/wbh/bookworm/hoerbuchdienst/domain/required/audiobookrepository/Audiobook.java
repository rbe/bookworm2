/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

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

    @JsonView(AudiobookViews.Default.class)
    private String sourceDate;

    @JsonView(AudiobookViews.SearchIndex.class)
    private String sourcePublisher;

    public static final Audiobook UNKNOWN = new Audiobook();

    /**
     * master.smil/body/ref
     */
    @JsonView(AudiobookViews.Default.class)
    private List<Audiotrack> audiotracks = new ArrayList<>();

    public String getTitelnummer() {
        return titelnummer;
    }

    public void setTitelnummer(final String titelnummer) {
        this.titelnummer = titelnummer;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(final String setInfo) {
        this.setInfo = setInfo;
    }

    public String getNarrator() {
        return narrator;
    }

    public void setNarrator(final String narrator) {
        this.narrator = narrator;
    }

    public String getAudioformat() {
        return audioformat;
    }

    public void setAudioformat(final String audioformat) {
        this.audioformat = audioformat;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(final String compression) {
        this.compression = compression;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getTocItems() {
        return tocItems;
    }

    public void setTocItems(final int tocItems) {
        this.tocItems = tocItems;
    }

    public Duration getTimeInThisSmil() {
        return timeInThisSmil;
    }

    public void setTimeInThisSmil(final Duration timeInThisSmil) {
        this.timeInThisSmil = timeInThisSmil;
    }

    public Duration getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(final Duration totalTime) {
        this.totalTime = totalTime;
    }

    public String getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(final String sourceDate) {
        this.sourceDate = sourceDate;
    }

    public String getSourcePublisher() {
        return sourcePublisher;
    }

    public void setSourcePublisher(final String sourcePublisher) {
        this.sourcePublisher = sourcePublisher;
    }

    public List<Audiotrack> getAudiotracks() {
        return new ArrayList<>(audiotracks);
    }

    public void setTocItems(final String tocItems) {
        this.tocItems = Integer.parseInt(tocItems);
    }

    public void addAudiotrack(final Audiotrack audiotrack) {
        this.audiotracks.add(audiotrack);
    }

    @Override
    public String toString() {
        return String.format("Audiobook{titelnummer='%s', identifier='%s', format='%s', publisher='%s', date='%s', language='%s', isbn='%s', author='%s', setInfo='%s', narrator='%s', audioformat='%s', compression='%s', title='%s', tocItems=%d, timeInThisSmil=%s, totalTime=%s, sourceDate='%s', sourcePublisher='%s', audiotracks=%s}",
                titelnummer, identifier, format, publisher, date, language, isbn, author, setInfo, narrator, audioformat, compression, title, tocItems, timeInThisSmil, totalTime, sourceDate, sourcePublisher, audiotracks);
    }

}
