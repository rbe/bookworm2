/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.katalog;

import java.io.Serializable;
import java.util.Arrays;

public final class TrackInfoAntwortDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private String titelnummer;

    private String ident;

    private String comment;

    private String artist;

    private String year;

    private String version;

    private int genre;

    private String genreDescription;

    private String composer;

    private String copyright;

    private String encoder;

    private String url;

    private int wmpRating;

    private int bpm;

    private int length;

    private int dataLength;

    private String padding;

    private boolean hasFooter;

    private boolean hasUnsynchronisation;

    private String grouping;

    private String key;

    private String date;

    private String publisher;

    private String originalArtist;

    private String albumArtist;

    private String artistUrl;

    private String commercialUrl;

    private String copyrightUrl;

    private String audiofileUrl;

    private String audioSourceUrl;

    private String radiostationUrl;

    private String paymentUrl;

    private String publisherUrl;

    private String partOfSet;

    private boolean compilation;

    private byte[] albumImage;

    private String albumImageMimeType;

    private String itunesComment;

    private String lyrics;

    private boolean obseleteFormat;

    public TrackInfoAntwortDTO() {
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public void setTitelnummer(final String titelnummer) {
        this.titelnummer = titelnummer;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(final String ident) {
        this.ident = ident;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(final int genre) {
        this.genre = genre;
    }

    public String getGenreDescription() {
        return genreDescription;
    }

    public void setGenreDescription(final String genreDescription) {
        this.genreDescription = genreDescription;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(final String composer) {
        this.composer = composer;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(final String copyright) {
        this.copyright = copyright;
    }

    public String getEncoder() {
        return encoder;
    }

    public void setEncoder(final String encoder) {
        this.encoder = encoder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public int getWmpRating() {
        return wmpRating;
    }

    public void setWmpRating(final int wmpRating) {
        this.wmpRating = wmpRating;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(final int bpm) {
        this.bpm = bpm;
    }

    public int getLength() {
        return length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(final int dataLength) {
        this.dataLength = dataLength;
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(final String padding) {
        this.padding = padding;
    }

    public boolean isHasFooter() {
        return hasFooter;
    }

    public void setHasFooter(final boolean hasFooter) {
        this.hasFooter = hasFooter;
    }

    public boolean isHasUnsynchronisation() {
        return hasUnsynchronisation;
    }

    public void setHasUnsynchronisation(final boolean hasUnsynchronisation) {
        this.hasUnsynchronisation = hasUnsynchronisation;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(final String grouping) {
        this.grouping = grouping;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getOriginalArtist() {
        return originalArtist;
    }

    public void setOriginalArtist(final String originalArtist) {
        this.originalArtist = originalArtist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(final String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    public void setArtistUrl(final String artistUrl) {
        this.artistUrl = artistUrl;
    }

    public String getCommercialUrl() {
        return commercialUrl;
    }

    public void setCommercialUrl(final String commercialUrl) {
        this.commercialUrl = commercialUrl;
    }

    public String getCopyrightUrl() {
        return copyrightUrl;
    }

    public void setCopyrightUrl(final String copyrightUrl) {
        this.copyrightUrl = copyrightUrl;
    }

    public String getAudiofileUrl() {
        return audiofileUrl;
    }

    public void setAudiofileUrl(final String audiofileUrl) {
        this.audiofileUrl = audiofileUrl;
    }

    public String getAudioSourceUrl() {
        return audioSourceUrl;
    }

    public void setAudioSourceUrl(final String audioSourceUrl) {
        this.audioSourceUrl = audioSourceUrl;
    }

    public String getRadiostationUrl() {
        return radiostationUrl;
    }

    public void setRadiostationUrl(final String radiostationUrl) {
        this.radiostationUrl = radiostationUrl;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(final String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public void setPublisherUrl(final String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public String getPartOfSet() {
        return partOfSet;
    }

    public void setPartOfSet(final String partOfSet) {
        this.partOfSet = partOfSet;
    }

    public boolean isCompilation() {
        return compilation;
    }

    public void setCompilation(final boolean compilation) {
        this.compilation = compilation;
    }

    public byte[] getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(final byte[] albumImage) {
        this.albumImage = albumImage;
    }

    public String getAlbumImageMimeType() {
        return albumImageMimeType;
    }

    public void setAlbumImageMimeType(final String albumImageMimeType) {
        this.albumImageMimeType = albumImageMimeType;
    }

    public String getItunesComment() {
        return itunesComment;
    }

    public void setItunesComment(final String itunesComment) {
        this.itunesComment = itunesComment;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public boolean isObseleteFormat() {
        return obseleteFormat;
    }

    public void setObseleteFormat(final boolean obseleteFormat) {
        this.obseleteFormat = obseleteFormat;
    }

    @Override
    public String toString() {
        return "TrackInfoAntwortDTO{" +
                "comment='" + comment + '\'' +
                ", artist='" + artist + '\'' +
                ", year='" + year + '\'' +
                ", version='" + version + '\'' +
                ", genre=" + genre +
                ", genreDescription='" + genreDescription + '\'' +
                ", composer='" + composer + '\'' +
                ", copyright='" + copyright + '\'' +
                ", encoder='" + encoder + '\'' +
                ", url='" + url + '\'' +
                ", wmpRating=" + wmpRating +
                ", bpm=" + bpm +
                ", length=" + length +
                ", dataLength=" + dataLength +
                ", padding='" + padding + '\'' +
                ", hasFooter=" + hasFooter +
                ", hasUnsynchronisation=" + hasUnsynchronisation +
                ", grouping='" + grouping + '\'' +
                ", key='" + key + '\'' +
                ", date='" + date + '\'' +
                ", publisher='" + publisher + '\'' +
                ", originalArtist='" + originalArtist + '\'' +
                ", albumArtist='" + albumArtist + '\'' +
                ", artistUrl='" + artistUrl + '\'' +
                ", commercialUrl='" + commercialUrl + '\'' +
                ", copyrightUrl='" + copyrightUrl + '\'' +
                ", audiofileUrl='" + audiofileUrl + '\'' +
                ", audioSourceUrl='" + audioSourceUrl + '\'' +
                ", radiostationUrl='" + radiostationUrl + '\'' +
                ", paymentUrl='" + paymentUrl + '\'' +
                ", publisherUrl='" + publisherUrl + '\'' +
                ", partOfSet='" + partOfSet + '\'' +
                ", compilation=" + compilation +
                ", albumImage=" + Arrays.toString(albumImage) +
                ", albumImageMimeType='" + albumImageMimeType + '\'' +
                ", itunesComment='" + itunesComment + '\'' +
                ", lyrics='" + lyrics + '\'' +
                ", obseleteFormat=" + obseleteFormat +
                '}';
    }

}
