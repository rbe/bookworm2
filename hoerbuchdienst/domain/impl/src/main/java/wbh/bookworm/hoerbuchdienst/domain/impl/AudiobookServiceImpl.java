/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import io.micronaut.context.annotation.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookService;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookServiceException;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.WatermarkedTrackInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;

import aoc.mikrokosmos.io.zip.Zip;

@Singleton
final class AudiobookServiceImpl implements AudiobookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookServiceImpl.class);

    // TODO Konfiguration pro Mandant
    private static final String PIRACY_INQUIRY_URL_PREFIX = "https://wbh-online.de/ausleihe-anfragen";

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final Zip zip;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    @Inject
    AudiobookServiceImpl(final AudiobookRepository audiobookRepository,
                         final Watermarker watermarker,
                         final Zip zip) {
        this.audiobookRepository = audiobookRepository;
        this.watermarker = watermarker;
        this.zip = zip;
    }

    @Override
    public TrackInfoDTO trackInfo(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = makeTemporaryCopyOfTrack(hoerernummer, titelnummer, ident, "trackinfo");
        final WatermarkedTrackInfo watermarkedTrackInfo = watermarker.trackInfo(makeWatermark(hoerernummer, titelnummer),
                PIRACY_INQUIRY_URL_PREFIX, tempMp3File);
        tryDelete(tempMp3File);
        return TrackMapper.INSTANCE.convert(watermarkedTrackInfo, titelnummer, ident);
    }

    @Override
    public InputStream trackAsStream(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = makeTemporaryCopyOfTrack(hoerernummer, titelnummer, ident, "trackAsByteArray");
        final byte[] watermarkedMp3Track;
        try {
            watermarker.addWatermarkInPlace(makeWatermark(hoerernummer, titelnummer),
                    PIRACY_INQUIRY_URL_PREFIX, tempMp3File);
            watermarkedMp3Track = Files.readAllBytes(tempMp3File);
        } catch (IOException e) {
            throw new AudiobookServiceException(e);
        }
        tryDelete(tempMp3File);
        return new ByteArrayInputStream(watermarkedMp3Track);
    }

    // TODO Lasttest mit n Hörbüchern und n Threads, wobei n=1,2,4,6,8,...
    // TODO Wasserzeichen anbringen mit 1 Thread pro MP3
    // TODO Bis zu wie vielen Threads steigert sich die Anzahl personalisierter Hörbücher?
    @Override
    public InputStream zipAsStream(final String hoerernummer, final String titelnummer) {
        // TODO Konfiguration
        final String javaIoTmpdir = System.getProperty("java.io.tmpdir");
        final Path audiobookDirectory = Path.of(String.format("%s%s", javaIoTmpdir, titelnummer));
        // ZIP auf tmpfs auspacken
        try (final ZipInputStream sourceZipStream = new ZipInputStream(audiobookRepository.zipAsStream(titelnummer))) {
            LOGGER.info("Entpacke Hörbuch {} auf tmpfs {}", titelnummer, javaIoTmpdir);
            zip.unzip(sourceZipStream, audiobookDirectory);
        } catch (IOException e) {
            throw new AudiobookServiceException(e);
        }
        final String watermark = makeWatermark(hoerernummer, titelnummer);
        // Wasserzeichen an MP3s anbringen
        // TODO "Kapitel" ist mandantenspezifisch
        final Path kapitelDirectory = audiobookDirectory.resolve(String.format("%sKapitel", titelnummer));
        try (final Stream<Path> paths = Files.list(kapitelDirectory)
                .filter(path -> path.getFileName().toString().endsWith(".mp3"))) {
            paths.collect(Collectors.toUnmodifiableList())
                    .parallelStream()
                    .forEach(mp3File -> {
                        LOGGER.info("Bringe Wasserzeichen {} in {} an", watermark, mp3File.getFileName());
                        watermarker.addWatermarkInPlace(watermark, PIRACY_INQUIRY_URL_PREFIX, mp3File);
                    });
        } catch (IOException e) {
            throw new AudiobookServiceException(e);
        }
        // Wasserzeichen als Textdatei in ZIP legen
        LOGGER.info("Lege Wasserzeichen {} als Textdatei in DAISY ZIP", watermark);
        try {
            Files.write(kapitelDirectory.resolve("cpr.txt"), watermark.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new AudiobookServiceException(e);
        }
        // Alle Dateien in DAISY ZIP packen
        final InputStream zip;
        try (final Stream<Path> paths = Files.list(kapitelDirectory)) {
            final List<Path> files = paths.collect(Collectors.toUnmodifiableList());
            LOGGER.info("Erstelle DAISY Hörbuch {} mit folgenden Dateien: {}", titelnummer, files);
            zip = this.zip.zipAsStream(files);
        } catch (IOException e) {
            throw new AudiobookServiceException(e);
        }
        tryDelete(audiobookDirectory);
        return zip;
    }

    private String makeWatermark(final String hoerernummer, final String titelnummer) {
        return String.format("WBH-%s-%s", hoerernummer, titelnummer);
    }

    private Path makeTemporaryCopyOfTrack(final String hoerernummer,
                                          final String titelnummer, final String ident,
                                          final String temporaryId) {
        // TODO "Kapitel" Suffix ist mandantenspezifisch
        final String tempId = String.format("%sKapitel-%s-%s-%s", titelnummer, ident, UUID.randomUUID(), temporaryId);
        final Path tempMp3File = temporaryDirectory.resolve(hoerernummer).resolve(tempId);
        try {
            Files.createDirectories(tempMp3File.getParent());
        } catch (IOException e) {
            throw new AudiobookServiceException(e);
        }
        try (final InputStream trackAsStream = audiobookRepository.trackAsStream(titelnummer, ident);
             final OutputStream tempMp3Stream = Files.newOutputStream(tempMp3File, StandardOpenOption.CREATE)) {
            trackAsStream.transferTo(tempMp3Stream);
            return tempMp3File;
        } catch (IOException e) {
            throw new AudiobookServiceException(e);
        }
    }

    private void tryDelete(final Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Mapper
    public interface TrackMapper {

        TrackMapper INSTANCE = Mappers.getMapper(TrackMapper.class);

        @Mapping(source = "titelnummer", target = "titelnummer")
        @Mapping(source = "ident", target = "ident")
        TrackInfoDTO convert(WatermarkedTrackInfo watermarkedTrackInfo, String titelnummer, String ident);

    }

}
