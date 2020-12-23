package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        streamToFile(zipFile, inputStream);
        return zipFile;
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
            copyMp3sToTempDirectory(hoerernummer, titelnummer, daisyDirectory);
            // Wasserzeichen an MP3s anbringen
            final String watermark = watermarker.makeWatermark(mandant, hoerernummer, titelnummer);
            final int numMp3s = watermarkMp3s(watermark, daisyDirectory);
            LOGGER.info("Hörer {} Hörbuch {}: Wasserzeichen {} in {} MP3s angebracht", hoerernummer, titelnummer,
                    watermark, numMp3s);
            // Wasserzeichen als Textdatei in ZIP legen
            addWatermarkFile(watermark, daisyDirectory);
            LOGGER.info("Hörer {} Hörbuch {}: Wasserzeichen {} als cpr.txt-Textdatei erstellt", hoerernummer, titelnummer, watermark);
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

    private void copyMp3sToTempDirectory(final String hoerernummer, final String titelnummer, final Path daisyDirectory) {
        final long start = System.nanoTime();
        audiobookRepository.mp3ToTempDirectory(titelnummer, daisyDirectory);
        final long stop = System.nanoTime();
        LOGGER.info("Hörer {} Hörbuch {} unter {} in {} s abgelegt", hoerernummer, titelnummer, temporaryDirectory,
                (stop - start) / 1_000_000L / 1_000L);
    }

    private int watermarkMp3s(final String watermark, final Path daisyDirectory) {
        final long start = System.nanoTime();
        try (final Stream<Path> pathStream = Files.list(daisyDirectory)
                .filter(path -> path.getFileName().toString().endsWith(".mp3"))) {
            final List<Path> paths = pathStream.collect(Collectors.toUnmodifiableList());
            paths.parallelStream() // Wasserzeichen anbringen mit 1 Thread pro MP3
                    .forEach(mp3File -> watermarker.addWatermarkInPlace(watermark, piracyInquiryUrlPrefix, mp3File));
            final long stop = System.nanoTime();
            LOGGER.info("Wasserzeichen an {} MP3s in {} in {} s angebracht", paths.size(), temporaryDirectory,
                    (stop - start) / 1_000_000L / 1_000L);
            return paths.size();
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

    private void addWatermarkFile(final String watermark, final Path daisyDirectory) {
        try {
            Files.writeString(daisyDirectory.resolve("cpr.txt"), watermark);
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

    private Path daisyZipAsFile(final String hoerernummer, final String titelnummer, final Path daisyDirectory) {
        final long start = System.nanoTime();
        try {
            final InputStream zipInputStream = daisyZipAsStream(hoerernummer, titelnummer, daisyDirectory);
            final Path zipFile = temporaryDirectory.resolve(UUID.randomUUID() + ".zip");
            streamToFile(zipFile, zipInputStream);
            final long stop = System.nanoTime();
            LOGGER.info("Hörer {} Hörbuch {}: {} MB in {} in {} s geschrieben", hoerernummer, titelnummer,
                    Files.size(zipFile) / 1_024L / 1_024L, zipFile, (stop - start) / 1_000_000L / 1_000L);
            return zipFile;
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
            LOGGER.info("Hörer {} Hörbuch {}: DAISY Hörbuch als Stream in {} s erstellt", hoerernummer, titelnummer,
                    (stop - start) / 1_000_000L / 1_000L);
            return zipInputStream;
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

    private void streamToFile(final Path zipFile, final InputStream zipInputStream) {
        try(final OutputStream outputStream = Files.newOutputStream(zipFile)) {
            zipInputStream.transferTo(outputStream);
            zipInputStream.close();
        } catch (Exception e) {
            throw new AudiobookServiceException("", e);
        }
    }

}
