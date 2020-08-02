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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Async;
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
class AudiobookServiceImpl implements AudiobookService {

    private static final String UNKNOWN = "unknown";

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookServiceImpl.class);

    private static final String DAISY_ZIP = "DAISY.zip";

    private static final String ORDER_STATUS_PROCESSING = "PROCESSING";

    private static final String ORDER_STATUS_SUCCESS = "SUCCESS";

    private static final String ORDER_STATUS_FAILED = "FAILED";

    private static final String ORDER_STATUS_UNKNOWN = "UNKNOWN";

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final Zip zip;

    private final Map<String, String> orderStatus;

    @Value(/* TODO Mandantenspezifisch */"${hoerbuchdienst.piracy.inquiry.urlprefix}")
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
        orderStatus = new ConcurrentHashMap<>(10);
    }

    @Override
    public String shardLocation(/* TODO Mandantenspezifisch */final String titelnummer) {
        final Optional<ShardName> maybeShardName = audiobookRepository.lookupShard(titelnummer);
        if (maybeShardName.isPresent()) {
            LOGGER.debug("Looked up shard '{}' for '{}'", maybeShardName, titelnummer);
            return maybeShardName.get().toString();
        } else {
            LOGGER.warn("Could not lookup shard for {}", titelnummer);
        }
        return UNKNOWN;
    }

    @Override
    public boolean isLocatedLocal(/* TODO Mandantenspezifisch */final String titelnummer) {
        final Optional<ShardName> maybeShardName = audiobookRepository.lookupShard(titelnummer);
        if (maybeShardName.isPresent()) {
            return new ShardName().equals(maybeShardName.get());
        } else {
            LOGGER.warn("Could not lookup shard for {}", titelnummer);
        }
        return false;
    }

    @Override
    public InputStream trackAsStream(final String hoerernummer, /* TODO Mandantenspezifisch */ final String titelnummer, final String ident) {
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
    public InputStream zipAsStream(final String hoerernummer, /* TODO Mandantenspezifisch */ final String titelnummer) {
        final Path audiobookDirectory = Path.of(String.format("%s/%s-%s-%s", temporaryDirectory, hoerernummer, titelnummer, UUID.randomUUID())
                .replace("//", "/"));
        try {
            Files.createDirectories(audiobookDirectory);
        } catch (IOException e) {
            throw new AudiobookServiceException("Cannot create temporary directory", e);
        }
        // ZIP auf tmpfs auspacken
        try (final ZipInputStream sourceZipStream = new ZipInputStream(audiobookRepository.zipAsStream(titelnummer))) {
            LOGGER.info("Hörer {} Entpacke Hörbuch {} unter {}", hoerernummer, titelnummer, temporaryDirectory);
            zip.unzip(sourceZipStream, audiobookDirectory);
            LOGGER.info("Hörer {} Hörbuch {} unter {} entpackt", hoerernummer, titelnummer, temporaryDirectory);
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
        LOGGER.info("Hörer {} Lege Wasserzeichen {} als Textdatei in DAISY ZIP", hoerernummer, watermark);
        try {
            Files.write(kapitelDirectory.resolve("Urheberrecht.txt"), watermark.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
            throw new AudiobookServiceException("", e);
        }
        // Alle Dateien in DAISY ZIP packen
        final InputStream zipInputStream;
        try (final Stream<Path> paths = Files.list(kapitelDirectory)) {
            final List<Path> files = paths.sorted().collect(Collectors.toUnmodifiableList());
            LOGGER.debug("Hörer {} Erstelle DAISY Hörbuch {} mit folgenden Dateien: {}", hoerernummer, titelnummer, files);
            zipInputStream = zip.zipAsStream(files);
            LOGGER.info("Hörer {} DAISY Hörbuch {} erstellt", hoerernummer, titelnummer);
            return zipInputStream;
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        } finally {
            FilesUtils.cleanupTemporaryDirectory(/* TODO Mandantenspezifisch */kapitelDirectory);
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
    }

    @Override
    @Async
    public void orderZip(final String hoerernummer, /* TODO Mandantenspezifisch */ final String titelnummer, final String orderId) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörbuch mit Wasserzeichen als ZIP",
                hoerernummer, titelnummer);
        orderStatus.put(orderId, ORDER_STATUS_PROCESSING);
        try (final InputStream audiobook = zipAsStream(hoerernummer, titelnummer)) {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Hörbuch mit Wasserzeichen als ZIP erstellt",
                    hoerernummer, titelnummer);
            final Path orderDirectory = temporaryDirectory.resolve(orderId);
            Files.createDirectories(orderDirectory);
            Files.write(orderDirectory.resolve(DAISY_ZIP), audiobook.readAllBytes());
            orderStatus.put(orderId, ORDER_STATUS_SUCCESS);
        } catch (Exception e) {
            orderStatus.put(orderId, ORDER_STATUS_FAILED);
            throw new AudiobookServiceException(String.format("Hörer %s Hörbuch %s: Kann Bestellung nicht persistieren", hoerernummer, titelnummer), e);
        }
    }

    @Override
    public String orderStatus(final String orderId) {
        return orderStatus.getOrDefault(orderId, UNKNOWN);
    }

    @Override
    public InputStream fetchOrder(final String orderId) {
        try {
            final Path orderDirectory = temporaryDirectory.resolve(orderId);
            final InputStream inputStream = Files.newInputStream(orderDirectory.resolve(DAISY_ZIP));
            orderStatus.remove(orderId);
            FilesUtils.cleanupTemporaryDirectory(orderDirectory);
            return inputStream;
        } catch (IOException e) {
            throw new AudiobookServiceException(String.format("Bestellung %s kann nicht abgerufen werden", orderId), e);
        }
    }

    @Override
    public boolean putZip(/* TODO Mandantenspezifisch */ final String titelnummer, final InputStream inputStream, final String hash) {
        return audiobookRepository.putZip(inputStream, new Titelnummer(titelnummer));
    }

}
