/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.watermark;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.watermark.WatermarkedTrackInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.WatermarkerException;

@Singleton
public final class WatermarkerMp3agicImpl implements Watermarker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatermarkerMp3agicImpl.class);

    private static final String GENRE_SPEECH = "Speech";

    @Override
    public String makeWatermark(final String mandant, final String hoerernummer, final String titelnummer) {
        LOGGER.debug("Erzeuge Wasserzeichen für Mandant '{}', Hörer '{}', Titelnummer '{}'",
                mandant, hoerernummer, titelnummer);
        return String.format("%s-%s-%s", mandant, hoerernummer, titelnummer);
    }

    @Override
    public WatermarkedTrackInfo trackInfo(final String watermark, final String urlPrefix, final Path mp3) {
        addWatermarkInPlace(watermark, urlPrefix, mp3);
        try {
            final Mp3File mp3file = new Mp3File(mp3);
            final ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            final ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            return new WatermarkedTrackInfo(
                    // ID3v1
                    id3v1Tag.getComment(),
                    id3v1Tag.getArtist(),
                    id3v1Tag.getYear(),
                    id3v1Tag.getVersion(),
                    id3v1Tag.getGenre(),
                    // ID3v2
                    id3v2Tag.getPadding(),
                    id3v2Tag.hasFooter(),
                    id3v2Tag.hasUnsynchronisation(),
                    id3v2Tag.getBPM(),
                    id3v2Tag.getGrouping(),
                    id3v2Tag.getKey(),
                    id3v2Tag.getDate(),
                    id3v2Tag.getComposer(),
                    id3v2Tag.getPublisher(),
                    id3v2Tag.getOriginalArtist(),
                    id3v2Tag.getAlbumArtist(),
                    id3v2Tag.getCopyright(),
                    id3v2Tag.getArtistUrl(),
                    id3v2Tag.getCommercialUrl(),
                    id3v2Tag.getCopyrightUrl(),
                    id3v2Tag.getAudiofileUrl(),
                    id3v2Tag.getAudioSourceUrl(),
                    id3v2Tag.getRadiostationUrl(),
                    id3v2Tag.getPaymentUrl(),
                    id3v2Tag.getPublisherUrl(),
                    id3v2Tag.getUrl(),
                    id3v2Tag.getPartOfSet(),
                    id3v2Tag.isCompilation(),
                    /*mp3file.getId3v2Tag().getChapters(),
                    mp3file.getId3v2Tag().getChapterTOC(),*/
                    id3v2Tag.getEncoder(),
                    id3v2Tag.getAlbumImage(),
                    id3v2Tag.getAlbumImageMimeType(),
                    id3v2Tag.getWmpRating(),
                    id3v2Tag.getItunesComment(),
                    id3v2Tag.getLyrics(),
                    id3v2Tag.getGenreDescription(),
                    id3v2Tag.getDataLength(),
                    id3v2Tag.getLength(),
                    id3v2Tag.getObseleteFormat()/*,
                    mp3file.getId3v2Tag().getFrameSets()*/
            );
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new WatermarkerException("", e);
        }
    }

    @Override
    public void addWatermarkInPlace(final String watermark, final String urlPrefix, final Path mp3) {
        final String filename = String.format("%s.watermark.%s", mp3.getFileName(), UUID.randomUUID());
        final Path watermarkedMp3File = mp3.getParent().resolve(filename);
        try {
            final Mp3File mp3file = new Mp3File(mp3.toFile());
            addWatermarkToId3Tags(watermark, urlPrefix, mp3file);
            mp3file.save(watermarkedMp3File.toAbsolutePath().toString());
            Files.move(watermarkedMp3File, mp3, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | NotSupportedException | UnsupportedTagException | InvalidDataException e) {
            throw new WatermarkerException("", e);
        }
    }

    private void addWatermarkToId3Tags(final String watermark, final String urlPrefix, final Mp3File mp3file) {
        final String watermarkUrl = String.format("%s/%s", urlPrefix, watermark);
        id3v1(watermark, watermarkUrl, mp3file);
        id3v2(watermark, watermarkUrl, mp3file);
    }

    private void id3v2(final String watermark, final String watermarkUrl, final Mp3File mp3file) {
        final ID3v2 id3v2Tag = new ID3v23Tag();
        if (!mp3file.hasId3v2Tag()) {
            mp3file.setId3v2Tag(id3v2Tag);
        }
        // TCON (Content type): Speech (101)
        id3v2Tag.setGenreDescription(GENRE_SPEECH);
        // TCOP (Copyright message), max. 30 Zeichen
        id3v2Tag.setCopyright(watermark);
        // WCOM (Commercial information)
        //id3v2Tag.setCommercialUrl();
        // WCOP (Copyright/Legal infromation)
        id3v2Tag.setCopyrightUrl(watermarkUrl);
        // WOAF (Official audio file webpage)
        //id3v2Tag.setAudiofileUrl();
        // WOAR (Official artist/performer webpage)
        //id3v2Tag.setArtistUrl();
        // WOAS (Official audio source webpage)
        //id3v2Tag.setAudioSourceUrl();
        // WPUB (Official publisher webpage)
        //id3v2Tag.setPublisherUrl("https://wbh-online.de");
        // WPAY (Payment)
        //id3v2Tag.setPaymentUrl();
        // WXXX (User defined URL link)
        //id3v2Tag.setUrl();
        // COMM (Comment)
        id3v2Tag.setComment(watermark);
    }

    private void id3v1(final String watermark, final String watermarkUrl, final Mp3File mp3file) {
        final ID3v1 id3v1Tag = new ID3v1Tag();
        if (!mp3file.hasId3v1Tag()) {
            mp3file.setId3v1Tag(id3v1Tag);
        }
        id3v1Tag.setGenre(ID3v1Genres.matchGenreDescription(GENRE_SPEECH));
        id3v1Tag.setComment(watermark); // max 30 Zeichen
    }

}
