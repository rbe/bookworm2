/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.Mp3RepositoryException;
import wbh.bookworm.hoerbuchdienst.domain.ports.Mp3RepositoryService;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackDTO;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookRepository;

@Singleton
final class Mp3RepositoryServiceImpl implements Mp3RepositoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mp3RepositoryServiceImpl.class);

    private final AudiobookRepository audiobookRepository;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    @Inject
    Mp3RepositoryServiceImpl(final AudiobookRepository audiobookRepository) {
        this.audiobookRepository = audiobookRepository;
    }

    @Override
    public TrackDTO trackInfo(final String hoerernummer, final String titelnummer, final String ident) {
        final InputStream trackAsStream = audiobookRepository.track(hoerernummer, titelnummer, ident);
        final Path tempMp3File = temporaryDirectory
                .resolve(hoerernummer)
                .resolve(String.format("%sKapitel", titelnummer))
                .resolve(String.format("%s.trackInfo", ident));
        try {
            Files.createDirectories(tempMp3File.getParent());
        } catch (IOException e) {
            throw new Mp3RepositoryException(e);
        }
        try (final OutputStream tempMp3Stream = Files.newOutputStream(tempMp3File, StandardOpenOption.CREATE)) {
            trackAsStream.transferTo(tempMp3Stream);
        } catch (IOException e) {
            throw new Mp3RepositoryException(e);
        }
        final TrackDTO trackDTO;
        try {
            final Mp3File mp3file = new Mp3File(tempMp3File);
            addWatermark(hoerernummer, titelnummer, mp3file);
            trackDTO = new TrackDTO(titelnummer, ident,
                    mp3file.getId3v1Tag().getComment(),
                    mp3file.getId3v1Tag().getArtist(),
                    mp3file.getId3v1Tag().getYear(),
                    mp3file.getId3v1Tag().getVersion(),
                    mp3file.getId3v1Tag().getGenre(),
                    mp3file.getId3v1Tag().getGenreDescription(),
                    mp3file.getId3v2Tag().getComposer(),
                    mp3file.getId3v2Tag().getCopyright(),
                    mp3file.getId3v2Tag().getEncoder(),
                    mp3file.getId3v2Tag().getUrl(),
                    mp3file.getId3v2Tag().getWmpRating(),
                    mp3file.getId3v2Tag().getBPM(),
                    mp3file.getId3v2Tag().getLength(),
                    mp3file.getId3v2Tag().getDataLength());
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new Mp3RepositoryException(e);
        }
        try {
            Files.delete(tempMp3File);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return trackDTO;
    }

    @Override
    public byte[] track(final String hoerernummer, final String titelnummer, final String ident) {
        final InputStream trackAsStream = audiobookRepository.track(hoerernummer, titelnummer, ident);
        final Path tempMp3File = temporaryDirectory
                .resolve(hoerernummer)
                .resolve(String.format("%sKapitel", titelnummer))
                .resolve(String.format("%s.track", ident));
        try {
            Files.createDirectories(tempMp3File.getParent());
        } catch (IOException e) {
            throw new Mp3RepositoryException(e);
        }
        try (final OutputStream tempMp3Stream = Files.newOutputStream(tempMp3File, StandardOpenOption.CREATE)) {
            trackAsStream.transferTo(tempMp3Stream);
        } catch (IOException e) {
            throw new Mp3RepositoryException(e);
        }
        final Mp3File mp3file;
        byte[] watermarkedMp3 = null;
        final Path watermarkedMp3File = tempMp3File.getParent()
                .resolve(tempMp3File.getFileName() + ".watermark");
        try {
            mp3file = new Mp3File(tempMp3File);
            addWatermark(hoerernummer, titelnummer, mp3file);
            mp3file.save(watermarkedMp3File.toAbsolutePath().toString());
            watermarkedMp3 = Files.readAllBytes(watermarkedMp3File);
        } catch (IOException | NotSupportedException | UnsupportedTagException | InvalidDataException e) {
            throw new Mp3RepositoryException(e);
        }
        try {
            Files.delete(tempMp3File);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        try {
            Files.delete(watermarkedMp3File);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return watermarkedMp3;
    }

    private void addWatermark(final String hoerernummer, final String titelnummer, final Mp3File mp3file) {
        final String watermark = String.format("WBH-%s-%s", hoerernummer, titelnummer);
        mp3file.getId3v1Tag().setComment(watermark); // max 30 Zeichen
        mp3file.getId3v2Tag().setCopyright(watermark); // max 30 Zeichen
        mp3file.getId3v2Tag().setUrl(String.format("https://wbh-online.de/ausleihe-anfragen/%s", watermark));
    }

}
