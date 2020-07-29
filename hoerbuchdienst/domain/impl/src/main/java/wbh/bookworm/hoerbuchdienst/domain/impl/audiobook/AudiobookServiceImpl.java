/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl.audiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import io.micronaut.context.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookService;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookServiceException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.io.fs.FilesUtils;
import aoc.mikrokosmos.io.zip.Zip;

@Singleton
final class AudiobookServiceImpl implements AudiobookService {

    public static final String UNKNOWN = "unknown";

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookServiceImpl.class);

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final Zip zip;

    // TODO Mandantenspezifisch
    @Value("${hoerbuchdienst.piracy.inquiry.urlprefix}")
    private String piracyInquiryUrlPrefix;

    @Value("${hoerbuchdienst.temporary.path}")
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
    public String shardLocation(final String titelnummer) {
        final ShardName shardName = audiobookRepository.lookupShard(titelnummer);
        LOGGER.debug("Looked up shard '{}' for '{}'", shardName, titelnummer);
        return null != shardName
                ? shardName.toString()
                : UNKNOWN;
    }

    @Override
    public InputStream trackAsStream(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = audiobookRepository.makeLocalCopyOfTrack(hoerernummer, titelnummer, ident,
                "trackAsByteArray");
        try {
            watermarker.addWatermarkInPlace(watermarker.makeWatermark(hoerernummer, titelnummer),
                    piracyInquiryUrlPrefix, tempMp3File);
            final byte[] watermarkedMp3Track = Files.readAllBytes(tempMp3File);
            FilesUtils.tryDelete(tempMp3File);
            return new ByteArrayInputStream(watermarkedMp3Track);
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

    // TODO Lasttest mit n Hörbüchern und n Threads, wobei n=1,2,4,6,8,...
    // TODO Bis zu wie vielen Threads steigert sich die Anzahl personalisierter Hörbücher?
    @Override
    public InputStream zipAsStream(final String hoerernummer, final String titelnummer) {
        final Path audiobookDirectory = Path.of(String.format("%s/%s", temporaryDirectory, titelnummer)
                .replace("//", "/"));
        // ZIP auf tmpfs auspacken
        try (final ZipInputStream sourceZipStream = new ZipInputStream(audiobookRepository.zipAsStream(titelnummer))) {
            LOGGER.info("Entpacke Hörbuch {} unter {}", titelnummer, temporaryDirectory);
            zip.unzip(sourceZipStream, audiobookDirectory);
            LOGGER.info("Hörbuch {} unter {} entpackt", titelnummer, temporaryDirectory);
        } catch (IOException e) {
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
            throw new AudiobookServiceException("", e);
        }
        final String watermark = watermarker.makeWatermark(hoerernummer, titelnummer);
        // Wasserzeichen an MP3s anbringen
        final Path kapitelDirectory = audiobookDirectory.resolve(/* TODO "Kapitel" ist mandantenspezifisch */String.format("%sKapitel", titelnummer));
        try (final Stream<Path> paths = Files.list(kapitelDirectory)
                .filter(path -> path.getFileName().toString().endsWith(".mp3"))) {
            paths.collect(Collectors.toUnmodifiableList())
                    // Wasserzeichen anbringen mit 1 Thread pro MP3
                    .parallelStream()
                    .forEach(mp3File -> {
                        LOGGER.info("Bringe Wasserzeichen {} in {} an", watermark, mp3File.getFileName());
                        watermarker.addWatermarkInPlace(watermark, piracyInquiryUrlPrefix, mp3File);
                    });
        } catch (IOException e) {
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
            throw new AudiobookServiceException("", e);
        }
        // Wasserzeichen als Textdatei in ZIP legen
        LOGGER.info("Lege Wasserzeichen {} als Textdatei in DAISY ZIP", watermark);
        try {
            Files.write(kapitelDirectory.resolve("Urheberrecht.txt"), watermark.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
            throw new AudiobookServiceException("", e);
        }
        // Alle Dateien in DAISY ZIP packen
        final InputStream zipInputStream;
        try (final Stream<Path> paths = Files.list(kapitelDirectory)) {
            final List<Path> files = paths.collect(Collectors.toUnmodifiableList());
            LOGGER.debug("Erstelle DAISY Hörbuch {} mit folgenden Dateien: {}", titelnummer, files);
            zipInputStream = zip.zipAsStream(files);
            LOGGER.info("DAISY Hörbuch {} mit folgenden Dateien: {} erstellt", titelnummer, files);
            return zipInputStream;
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        } finally {
            FilesUtils.cleanupTemporaryDirectory(/* TODO Mandantenspezifisch */kapitelDirectory);
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
    }

    @Override
    public boolean putZip(final String titelnummer, final InputStream inputStream, final String hash) {
        return audiobookRepository.putZip(inputStream, new Titelnummer(titelnummer));
    }

}
