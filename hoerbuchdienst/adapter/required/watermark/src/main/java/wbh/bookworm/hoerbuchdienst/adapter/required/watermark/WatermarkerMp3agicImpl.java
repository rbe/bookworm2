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

import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import wbh.bookworm.hoerbuchdienst.domain.required.watermark.WatermarkedTrackInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.WatermarkerException;

@Singleton
public final class WatermarkerMp3agicImpl implements Watermarker {

    private static final String GENRE_SPEECH = "Speech";

    @Override
    public WatermarkedTrackInfo trackInfo(final String watermark, final String urlPrefix, final Path mp3) {
        addWatermarkInPlace(watermark, urlPrefix, mp3);
        try {
            final Mp3File mp3file = new Mp3File(mp3);
            return new WatermarkedTrackInfo(
                    // ID3v1
                    mp3file.getId3v1Tag().getComment(),
                    mp3file.getId3v1Tag().getArtist(),
                    mp3file.getId3v1Tag().getYear(),
                    mp3file.getId3v1Tag().getVersion(),
                    mp3file.getId3v1Tag().getGenre(),
                    // ID3v2
                    mp3file.getId3v2Tag().getPadding(),
                    mp3file.getId3v2Tag().hasFooter(),
                    mp3file.getId3v2Tag().hasUnsynchronisation(),
                    mp3file.getId3v2Tag().getBPM(),
                    mp3file.getId3v2Tag().getGrouping(),
                    mp3file.getId3v2Tag().getKey(),
                    mp3file.getId3v2Tag().getDate(),
                    mp3file.getId3v2Tag().getComposer(),
                    mp3file.getId3v2Tag().getPublisher(),
                    mp3file.getId3v2Tag().getOriginalArtist(),
                    mp3file.getId3v2Tag().getAlbumArtist(),
                    mp3file.getId3v2Tag().getCopyright(),
                    mp3file.getId3v2Tag().getArtistUrl(),
                    mp3file.getId3v2Tag().getCommercialUrl(),
                    mp3file.getId3v2Tag().getCopyrightUrl(),
                    mp3file.getId3v2Tag().getAudiofileUrl(),
                    mp3file.getId3v2Tag().getAudioSourceUrl(),
                    mp3file.getId3v2Tag().getRadiostationUrl(),
                    mp3file.getId3v2Tag().getPaymentUrl(),
                    mp3file.getId3v2Tag().getPublisherUrl(),
                    mp3file.getId3v2Tag().getUrl(),
                    mp3file.getId3v2Tag().getPartOfSet(),
                    mp3file.getId3v2Tag().isCompilation(),
                    /*mp3file.getId3v2Tag().getChapters(),
                    mp3file.getId3v2Tag().getChapterTOC(),*/
                    mp3file.getId3v2Tag().getEncoder(),
                    mp3file.getId3v2Tag().getAlbumImage(),
                    mp3file.getId3v2Tag().getAlbumImageMimeType(),
                    mp3file.getId3v2Tag().getWmpRating(),
                    mp3file.getId3v2Tag().getItunesComment(),
                    mp3file.getId3v2Tag().getLyrics(),
                    mp3file.getId3v2Tag().getGenreDescription(),
                    mp3file.getId3v2Tag().getDataLength(),
                    mp3file.getId3v2Tag().getLength(),
                    mp3file.getId3v2Tag().getObseleteFormat()/*,
                    mp3file.getId3v2Tag().getFrameSets()*/
            );
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new WatermarkerException("", e);
        }
    }

    @Override
    public void addWatermarkInPlace(final String watermark, final String urlPrefix, final Path mp3) {
        final Path watermarkedMp3File = mp3.getParent()
                .resolve(String.format("%s.watermark.%s", mp3.getFileName(), UUID.randomUUID()));
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
        if (!mp3file.hasId3v1Tag()) {
            mp3file.setId3v1Tag(new ID3v1Tag());
        }
        if (!mp3file.hasId3v2Tag()) {
            mp3file.setId3v2Tag(new ID3v24Tag());
        }
        mp3file.getId3v1Tag().setGenre(ID3v1Genres.matchGenreDescription(GENRE_SPEECH));
        mp3file.getId3v1Tag().setComment(watermark); // max 30 Zeichen
        mp3file.getId3v2Tag().setGenreDescription(GENRE_SPEECH);
        mp3file.getId3v2Tag().setCopyright(watermark); // max 30 Zeichen
        mp3file.getId3v2Tag().setUrl(String.format("%s/%s", urlPrefix, watermark));
    }

}
