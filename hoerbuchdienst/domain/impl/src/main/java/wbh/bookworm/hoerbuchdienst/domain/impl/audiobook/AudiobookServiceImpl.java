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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookService;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookServiceException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.io.fs.FilesUtils;
import aoc.mikrokosmos.io.zip.Zip;

@Singleton
final class AudiobookServiceImpl implements AudiobookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookServiceImpl.class);

    // TODO Konfiguration pro Mandant
    private static final String PIRACY_INQUIRY_URL_PREFIX = "https://wbh-online.de/ausleihe-anfragen";

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final Zip zip;

    @Inject
    AudiobookServiceImpl(final AudiobookRepository audiobookRepository,
                         final Watermarker watermarker,
                         final Zip zip) {
        this.audiobookRepository = audiobookRepository;
        this.watermarker = watermarker;
        this.zip = zip;
    }

    @Override
    public int shardLocation(final String titelnummer) {
        return audiobookRepository.lookupShard(titelnummer).intValue();
    }

    @Override
    public InputStream trackAsStream(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = audiobookRepository.makeLocalCopyOfTrack(hoerernummer, titelnummer, ident,
                "trackAsByteArray");
        try {
            watermarker.addWatermarkInPlace(watermarker.makeWatermark(hoerernummer, titelnummer),
                    PIRACY_INQUIRY_URL_PREFIX, tempMp3File);
            final byte[] watermarkedMp3Track = Files.readAllBytes(tempMp3File);
            FilesUtils.tryDelete(tempMp3File);
            return new ByteArrayInputStream(watermarkedMp3Track);
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
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
            throw new AudiobookServiceException("", e);
        } finally {
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
        final String watermark = watermarker.makeWatermark(hoerernummer, titelnummer);
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
            throw new AudiobookServiceException("", e);
        } finally {
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
        // Wasserzeichen als Textdatei in ZIP legen
        LOGGER.info("Lege Wasserzeichen {} als Textdatei in DAISY ZIP", watermark);
        try {
            Files.write(kapitelDirectory.resolve("cpr.txt"), watermark.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
        // Alle Dateien in DAISY ZIP packen
        final InputStream zipInputStream;
        try (final Stream<Path> paths = Files.list(kapitelDirectory)) {
            final List<Path> files = paths.collect(Collectors.toUnmodifiableList());
            LOGGER.info("Erstelle DAISY Hörbuch {} mit folgenden Dateien: {}", titelnummer, files);
            zipInputStream = zip.zipAsStream(files);
            return zipInputStream;
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        } finally {
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
    }

    @Override
    public boolean putZip(final String titelnummer, final InputStream inputStream, final String hash) {
        return audiobookRepository.putZip(inputStream, new Titelnummer(titelnummer));
    }

}
