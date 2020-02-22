/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.infrastructure.smil;

import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public final class Audiobook implements Serializable {

    private static final long serialVersionUID = -1L;

    /** master.smil/head/dc:identifier */
    private String identifier;

    /**
     * z.B. "DAISY 2.02".
     */
    private String format;

    /**
     * dc:publisher, z.B. "WBH"
     */
    private String publisher;

    /**
     *
     */
    private String date;

    /**
     *
     */
    private String language;

    /**
     *
     */
    private String isbn;

    /**
     *
     */
    private String author;

    /**
     *
     */
    private String setInfo;

    /**
     * ncc.html/head/meta narrator.
     */
    private String narrator;

    /**
     * ncc.html/head/meta audioformat.
     */
    private String audioformat;

    /**
     * ncc.html/head/meta compression.
     */
    private String compression;

    /**
     * master.smil/head/dc:title
     */
    private String title;

    /**
     * ncc:tocItems
     */
    private int tocItems;

    /**
     * master.smil/head/ncc:timeInThisSmil.
     * Gesamte Länge des Hörbuchs.
     */
    private Duration duration;

    /**
     * ncc:totalTime
     */
    private Duration totalTime;

    /**
     * master.smil/body/ref
     */
    private Track[] tracks;

    private String sourceDate;

    private String sourcePublisher;

    public String getIdentifier() {
        return identifier;
    }

    void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getFormat() {
        return format;
    }

    void setFormat(final String format) {
        this.format = format;
    }

    public String getPublisher() {
        return publisher;
    }

    void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    void setDate(final String date) {
        this.date = date;
    }

    public String getLanguage() {
        return language;
    }

    void setLanguage(final String language) {
        this.language = language;
    }

    public String getIsbn() {
        return isbn;
    }

    void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    void setAuthor(final String author) {
        this.author = author;
    }

    public String getSetInfo() {
        return setInfo;
    }

    void setSetInfo(final String setInfo) {
        this.setInfo = setInfo;
    }

    public String getNarrator() {
        return narrator;
    }

    void setNarrator(final String narrator) {
        this.narrator = narrator;
    }

    public String getAudioformat() {
        return audioformat;
    }

    void setAudioformat(final String audioformat) {
        this.audioformat = audioformat;
    }

    public String getCompression() {
        return compression;
    }

    void setCompression(final String compression) {
        this.compression = compression;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(final String title) {
        this.title = title;
    }

    public int getTocItems() {
        return tocItems;
    }

    void setTocItems(final String tocItems) {
        this.tocItems = Integer.parseInt(tocItems);
    }

    public Duration getDuration() {
        return duration;
    }

    void setDuration(final Duration duration) {
        this.duration = duration;
    }

    public Duration getTotalTime() {
        return totalTime;
    }

    void setTotalTime(final Duration totalTime) {
        this.totalTime = totalTime;
    }

    public Track[] getTracks() {
        //return Arrays.copyOf(titles, titles.length);
        return tracks.clone();
    }

    void setTracks(final List<Track> tracks) {
        this.tracks = tracks.toArray(Track[]::new);
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

    @Override
    public String toString() {
        return String.format("Audiobook{identifier='%s', format='%s', publisher='%s', date='%s'," +
                        " isbn='%s', sourceDate='%s', sourcePublisher='%s'," +
                        " language='%s', isbn='%s', author='%s', setInfo='%s', narrator='%s'," +
                        " audioformat='%s', compression='%s', title='%s', tocItems=%d," +
                        " duration=%s, totalTime=%s, titles=%s}",
                identifier, format, publisher, date,
                isbn, sourceDate, sourcePublisher,
                language, isbn, author, setInfo, narrator,
                audioformat, compression, title, tocItems,
                duration, totalTime,
                Arrays.toString(tracks));
    }

}
