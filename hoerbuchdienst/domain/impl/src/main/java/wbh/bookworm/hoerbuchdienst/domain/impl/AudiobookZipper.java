package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.micronaut.context.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookServiceException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;

import aoc.mikrokosmos.io.fs.FilesUtils;
import aoc.mikrokosmos.io.stream.StreamException;
import aoc.mikrokosmos.io.stream.StreamsUtils;
import aoc.mikrokosmos.io.zip.Zip;

final class AudiobookZipper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookZipper.class);

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final Zip zip;

    @Value("${hoerbuchdienst.temporary.path}")
    private Path temporaryDirectory;

    @Value(/* TODO Mandantenspezifisch */"${hoerbuchdienst.piracy.inquiry.urlprefix}")
    private String piracyInquiryUrlPrefix;

    @Inject
    AudiobookZipper(final AudiobookRepository audiobookRepository, final Watermarker watermarker, final Zip zip) {
        this.audiobookRepository = audiobookRepository;
        this.watermarker = watermarker;
        this.zip = zip;
    }

    Path watermarkedDaisyZipAsFile(final String mandant, final String hoerernummer, final String titelnummer) {
        final InputStream inputStream = watermarkedDaisyZipAsStream(mandant, hoerernummer, titelnummer);
        final Path zipFile = temporaryDirectory.resolve(UUID.randomUUID() + ".zip");
        try {
            StreamsUtils.toFile(inputStream, zipFile);
            LOGGER.info("Hörer '{}' Hörbuch '{}': DAISY ZIP Stream wurde in '{}' gespeichert", hoerernummer, titelnummer, zipFile);
            return zipFile;
        } catch (StreamException e) {
            LOGGER.error(String.format("Hörer '%s' Hörbuch '%s': Fehler bei der Erzeugung des DAISY ZIP Streams",
                    hoerernummer, titelnummer), e);
            return null;
        }
    }

    InputStream watermarkedDaisyZipAsStream(final String mandant, final String hoerernummer, final String titelnummer) {
        final String tempName = String.format("%s/%s-%s-%s", temporaryDirectory, hoerernummer, titelnummer, UUID.randomUUID())
                .replace("//", "/");
        final Path audiobookDirectory = Path.of(tempName);
        try {
            Files.createDirectories(audiobookDirectory);
        } catch (IOException e) {
            final String message = String.format("Cannot create temporary directory %s", audiobookDirectory.toAbsolutePath());
            throw new AudiobookServiceException(message, e);
        }
        final Path daisyDirectory = audiobookDirectory.resolve(String.format("%sDAISY", titelnummer));
        try {
            // MP3s auf tmpfs packen
            final long start = System.nanoTime();
            audiobookRepository.mp3ToTempDirectory(titelnummer, daisyDirectory);
            final long stop = System.nanoTime();
            LOGGER.info("Hörer '{}' Hörbuch '{}': ZIP unter '{}' in {} ms = {} s abgelegt", hoerernummer, titelnummer, daisyDirectory,
                    (stop - start) / 1_000_000L, (stop - start) / 1_000_000L / 1_000L);
            // Wasserzeichen an MP3s anbringen
            final String watermark = watermarker.makeWatermark(mandant, hoerernummer, titelnummer);
            watermarkMp3s(watermark, daisyDirectory);
            // Wasserzeichen als Textdatei in ZIP legen
            addWatermarkFile(watermark, daisyDirectory);
            // Alle Dateien in DAISY ZIP packen
            return daisyZipAsStream(hoerernummer, titelnummer, daisyDirectory);
        } catch (AudiobookServiceException e) {
            throw e;
        } finally {
            // Cleanup
            FilesUtils.cleanupTemporaryDirectory(daisyDirectory);
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
    }

    private int watermarkMp3s(final String watermark, final Path daisyDirectory) {
        final long start = System.nanoTime();
        try (final Stream<Path> pathStream = Files.list(daisyDirectory)
                .filter(path -> path.getFileName().toString().endsWith(".mp3"))) {
            final List<Path> paths = pathStream.collect(Collectors.toUnmodifiableList());
            paths.parallelStream() // Wasserzeichen anbringen mit 1 Thread pro MP3
                    .forEach(mp3File -> watermarker.addWatermarkInPlace(watermark, piracyInquiryUrlPrefix, mp3File));
            final long stop = System.nanoTime();
            LOGGER.info("Wasserzeichen an {} MP3s in {} in {} ms = {} s angebracht", paths.size(), daisyDirectory,
                    (stop - start) / 1_000_000L, (stop - start) / 1_000_000L / 1_000L);
            return paths.size();
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

    private void addWatermarkFile(final String watermark, final Path daisyDirectory) {
        final long start = System.nanoTime();
        try {
            Files.writeString(daisyDirectory.resolve("cpr.txt"), watermark);
            final long stop = System.nanoTime();
            LOGGER.info("Wasserzeichen als {}/cpr.txt-Textdatei in {} ms erstellt",
                    daisyDirectory, (stop - start) / 1_000_000L);
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

    private InputStream daisyZipAsStream(final String hoerernummer, final String titelnummer, final Path daisyDirectory) {
        final long start = System.nanoTime();
        final InputStream zipInputStream;
        try (final Stream<Path> paths = Files.list(daisyDirectory)) {
            final List<Path> files = paths.sorted().collect(Collectors.toUnmodifiableList());
            zipInputStream = zip.zipAsStream(files);
            final long stop = System.nanoTime();
            LOGGER.info("Hörer '{}' Hörbuch '{}': DAISY Hörbuch als Stream in {} ms = {} s erstellt", hoerernummer, titelnummer,
                    (stop - start) / 1_000_000L, (stop - start) / 1_000_000L / 1_000L);
            return zipInputStream;
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

}
