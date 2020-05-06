/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.watermark;

import java.io.Serializable;

public final class WatermarkedTrackInfo implements Serializable {

    private static final long serialVersionUID = -1L;

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

    private final String padding;

    private final boolean hasFooter;

    private final boolean hasUnsynchronisation;

    private final String grouping;

    private final String key;

    private final String date;

    private final String publisher;

    private final String originalArtist;

    private final String albumArtist;

    private final String artistUrl;

    private final String commercialUrl;

    private final String copyrightUrl;

    private final String audiofileUrl;

    private final String audioSourceUrl;

    private final String radiostationUrl;

    private final String paymentUrl;

    private final String publisherUrl;

    private final String partOfSet;

    private final boolean compilation;

    private final byte[] albumImage;

    private final String albumImageMimeType;

    private final String itunesComment;

    private final String lyrics;

    private final boolean obseleteFormat;

    public WatermarkedTrackInfo(final String comment, final String artist, final String year,
                                final String version, final int genre,
                                final boolean padding, final boolean hasFooter, final boolean hasUnsynchronisation,
                                final int bpm, final String grouping, final String key, final String date,
                                final String composer, final String publisher, final String originalArtist,
                                final String albumArtist, final String copyright, final String artistUrl,
                                final String commercialUrl, final String copyrightUrl, final String audiofileUrl,
                                final String audioSourceUrl, final String radiostationUrl, final String paymentUrl,
                                final String publisherUrl, final String url, final String partOfSet,
                                final boolean compilation, final String encoder, final byte[] albumImage,
                                final String albumImageMimeType, final int wmpRating, final String itunesComment,
                                final String lyrics, final String genreDescription, final int dataLength,
                                final int length, final boolean obseleteFormat) {
        this.comment = toStringOrEmpty(comment);
        this.artist = toStringOrEmpty(artist);
        this.year = toStringOrEmpty(year);
        this.version = toStringOrEmpty(version);
        this.genre = genre;
        this.padding = toStringOrEmpty(padding);
        this.hasFooter = hasFooter;
        this.hasUnsynchronisation = hasUnsynchronisation;
        this.bpm = bpm;
        this.grouping = toStringOrEmpty(grouping);
        this.key = toStringOrEmpty(key);
        this.date = toStringOrEmpty(date);
        this.composer = toStringOrEmpty(composer);
        this.publisher = toStringOrEmpty(publisher);
        this.originalArtist = toStringOrEmpty(originalArtist);
        this.albumArtist = toStringOrEmpty(albumArtist);
        this.copyright = toStringOrEmpty(copyright);
        this.artistUrl = toStringOrEmpty(artistUrl);
        this.commercialUrl = toStringOrEmpty(commercialUrl);
        this.copyrightUrl = toStringOrEmpty(copyrightUrl);
        this.audiofileUrl = toStringOrEmpty(audiofileUrl);
        this.audioSourceUrl = toStringOrEmpty(audioSourceUrl);
        this.radiostationUrl = toStringOrEmpty(radiostationUrl);
        this.paymentUrl = toStringOrEmpty(paymentUrl);
        this.publisherUrl = toStringOrEmpty(publisherUrl);
        this.url = toStringOrEmpty(url);
        this.partOfSet = toStringOrEmpty(partOfSet);
        this.compilation = compilation;
        this.encoder = toStringOrEmpty(encoder);
        if (null != albumImage) {
            this.albumImage = albumImage.clone();
        } else {
            this.albumImage = null;
        }
        this.albumImageMimeType = toStringOrEmpty(albumImageMimeType);
        this.wmpRating = wmpRating;
        this.itunesComment = toStringOrEmpty(itunesComment);
        this.lyrics = toStringOrEmpty(lyrics);
        this.genreDescription = toStringOrEmpty(genreDescription);
        this.dataLength = dataLength;
        this.length = length;
        this.obseleteFormat = obseleteFormat;
    }

    private String toStringOrEmpty(final Object obj) {
        return null == obj ? "(no data)" : obj.toString();
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

    public String getPadding() {
        return padding;
    }

    public boolean isHasFooter() {
        return hasFooter;
    }

    public boolean isHasUnsynchronisation() {
        return hasUnsynchronisation;
    }

    public String getGrouping() {
        return grouping;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getOriginalArtist() {
        return originalArtist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    public String getCommercialUrl() {
        return commercialUrl;
    }

    public String getCopyrightUrl() {
        return copyrightUrl;
    }

    public String getAudiofileUrl() {
        return audiofileUrl;
    }

    public String getAudioSourceUrl() {
        return audioSourceUrl;
    }

    public String getRadiostationUrl() {
        return radiostationUrl;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public String getPartOfSet() {
        return partOfSet;
    }

    public boolean isCompilation() {
        return compilation;
    }

    public byte[] getAlbumImage() {
        return albumImage;
    }

    public String getAlbumImageMimeType() {
        return albumImageMimeType;
    }

    public String getItunesComment() {
        return itunesComment;
    }

    public String getLyrics() {
        return lyrics;
    }

    public boolean isObseleteFormat() {
        return obseleteFormat;
    }

    @Override
    public String toString() {
        return String.format("TrackInfoDTO{comment='%s', artist='%s', year='%s', version='%s', genre=%d, genreDescription='%s', composer='%s', copyright='%s', encoder='%s', url='%s', wmpRating=%d, bpm=%d, length=%d, dataLength=%d}",
                comment, artist, year, version, genre, genreDescription, composer, copyright, encoder, url, wmpRating, bpm, length, dataLength);
    }

}
