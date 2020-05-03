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
import java.util.UUID;

import io.micronaut.context.annotation.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.Mp3RepositoryException;
import wbh.bookworm.hoerbuchdienst.domain.ports.Mp3RepositoryService;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackDTO;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.TrackInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;

@Singleton
final class Mp3RepositoryServiceImpl implements Mp3RepositoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mp3RepositoryServiceImpl.class);

    private static final String URL_PREFIX = "https://wbh-online.de/ausleihe-anfragen";

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    @Inject
    Mp3RepositoryServiceImpl(final AudiobookRepository audiobookRepository,
                             final Watermarker watermarker) {
        this.audiobookRepository = audiobookRepository;
        this.watermarker = watermarker;
    }

    @Override
    public TrackDTO trackInfo(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = tempCopy(hoerernummer, titelnummer, ident, "trackinfo");
        final TrackInfoDTO trackInfoDTO = watermarker.trackInfo(makeWatermark(hoerernummer, titelnummer),
                URL_PREFIX, tempMp3File);
        tryDeleteFile(tempMp3File);
        return MyMapper.INSTANCE.convert(trackInfoDTO, titelnummer, ident);
    }

    @Override
    public byte[] track(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = tempCopy(hoerernummer, titelnummer, ident, "track");
        final Path watermarkedMp3File;
        final byte[] watermarkedMp3Track;
        try {
            watermarkedMp3File = watermarker.addWatermark(makeWatermark(hoerernummer, titelnummer),
                    URL_PREFIX, tempMp3File);
            watermarkedMp3Track = Files.readAllBytes(watermarkedMp3File);
        } catch (IOException e) {
            throw new Mp3RepositoryException(e);
        }
        tryDeleteFile(tempMp3File);
        tryDeleteFile(watermarkedMp3File);
        return watermarkedMp3Track;
    }

    private String makeWatermark(final String hoerernummer, final String titelnummer) {
        return String.format("WBH-%s-%s", hoerernummer, titelnummer);
    }

    private Path tempCopy(final String hoerernummer,
                          final String titelnummer, final String ident,
                          final String temp) {
        final InputStream trackAsStream = audiobookRepository.track(hoerernummer, titelnummer, ident);
        final Path tempMp3File = temporaryDirectory
                .resolve(hoerernummer)
                .resolve(String.format("%sKapitel-%s-%s-%s", titelnummer, ident, UUID.randomUUID(), temp));
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
        return tempMp3File;
    }

    private void tryDeleteFile(final Path tempMp3File) {
        try {
            Files.delete(tempMp3File);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Mapper
    public interface MyMapper {

        MyMapper INSTANCE = Mappers.getMapper(MyMapper.class);

        @Mapping(source = "titelnummer", target = "titelnummer")
        @Mapping(source = "ident", target = "ident")
        TrackDTO convert(TrackInfoDTO trackInfoDTO, String titelnummer, String ident);

    }

}
