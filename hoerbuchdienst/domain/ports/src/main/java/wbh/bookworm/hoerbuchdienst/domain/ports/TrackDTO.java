/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.Serializable;

public final class TrackDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String titelnummer;

    private final String ident;

    private final String comment;

    private final String artist;

    private final String year;

    private final String version;

    private final int genre;

    private final String genreDescription;

    private final String composer;

    private final String copyright;

    private final String encoder;

    private final String url;

    private final int wmpRating;

    private final int bpm;

    private final int length;

    private final int dataLength;

    public TrackDTO(final String titelnummer, final String ident,
                    final String comment, final String artist, final String year,
                    final String version, final int genre, final String genreDescription,
                    final String composer, final String copyright, final String encoder,
                    final String url, final int wmpRating, final int bpm,
                    final int length, final int dataLength) {
        this.titelnummer = toStringOrEmpty(titelnummer);
        this.ident = toStringOrEmpty(ident);
        this.comment = toStringOrEmpty(comment);
        this.artist = toStringOrEmpty(artist);
        this.year = toStringOrEmpty(year);
        this.version = toStringOrEmpty(version);
        this.genre = genre;
        this.genreDescription = toStringOrEmpty(genreDescription);
        this.composer = toStringOrEmpty(composer);
        this.copyright = toStringOrEmpty(copyright);
        this.encoder = toStringOrEmpty(encoder);
        this.url = toStringOrEmpty(url);
        this.wmpRating = wmpRating;
        this.bpm = bpm;
        this.length = length;
        this.dataLength = dataLength;
    }

    private String toStringOrEmpty(final Object obj) {
        return null == obj ? "(no data)" : obj.toString();
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public String getIdent() {
        return ident;
    }

    public String getComment() {
        return comment;
    }

    public String getArtist() {
        return artist;
    }

    public String getYear() {
        return year;
    }

    public String getVersion() {
        return version;
    }

    public int getGenre() {
        return genre;
    }

    public String getGenreDescription() {
        return genreDescription;
    }

    public String getComposer() {
        return composer;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getEncoder() {
        return encoder;
    }

    public String getUrl() {
        return url;
    }

    public int getWmpRating() {
        return wmpRating;
    }

    public int getBpm() {
        return bpm;
    }

    public int getLength() {
        return length;
    }

    public int getDataLength() {
        return dataLength;
    }

    @Override
    public String toString() {
        return String.format("TrackDTO{titelnummer='%s', ident='%s', comment='%s', artist='%s', year='%s', version='%s', genre=%d, genreDescription='%s', composer='%s', copyright='%s', encoder='%s', url='%s', wmpRating=%d, bpm=%d, length=%d, dataLength=%d}",
                titelnummer, ident, comment, artist, year, version, genre, genreDescription, composer, copyright, encoder, url, wmpRating, bpm, length, dataLength);
    }

}
