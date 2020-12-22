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

    Path watermarkedAudiobookAsZip(final String mandant, final String hoerernummer, final String titelnummer) {
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
        // MP3s auf tmpfs packen
        copyMp3sToTempDirectory(hoerernummer, titelnummer, daisyDirectory);
        try {
            // Wasserzeichen an MP3s anbringen
            final String watermark = watermarker.makeWatermark(mandant, hoerernummer, titelnummer);
            final int numMp3s = watermarkMp3s(watermark, daisyDirectory);
            LOGGER.info("Hörer {} Hörbuch {}: Wasserzeichen {} in {} MP3s angebracht", hoerernummer, titelnummer,
                    watermark, numMp3s);
            // Wasserzeichen als Textdatei in ZIP legen
            addWatermarkFile(watermark, daisyDirectory);
            LOGGER.info("Hörer {} Hörbuch {}: Wasserzeichen {} als cpr.txt-Textdatei erstellt", hoerernummer, titelnummer, watermark);
            // Alle Dateien in DAISY ZIP packen
            return toZip(hoerernummer, titelnummer, daisyDirectory);
        } catch (AudiobookServiceException e) {
            throw e;
        } finally {
            // Cleanup
            FilesUtils.cleanupTemporaryDirectory(daisyDirectory);
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
    }

    private void copyMp3sToTempDirectory(final String hoerernummer, final String titelnummer, final Path daisyDirectory) {
        final long startCopyMp3 = System.nanoTime();
        audiobookRepository.mp3ToTempDirectory(titelnummer, daisyDirectory);
        final long stopCopyMp3 = System.nanoTime();
        LOGGER.info("Hörer {} Hörbuch {} unter {} in {} s abgelegt", hoerernummer, titelnummer, temporaryDirectory,
                (stopCopyMp3 - startCopyMp3) / 1_000_000L / 1_000L);
    }

    private int watermarkMp3s(final String watermark, final Path daisyDirectory) {
        try (final Stream<Path> pathStream = Files.list(daisyDirectory)
                .filter(path -> path.getFileName().toString().endsWith(".mp3"))) {
            final List<Path> paths = pathStream.collect(Collectors.toUnmodifiableList());
            paths.parallelStream() // Wasserzeichen anbringen mit 1 Thread pro MP3
                    .forEach(mp3File -> {
                        watermarker.addWatermarkInPlace(watermark, piracyInquiryUrlPrefix, mp3File);
                    });
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

    private Path toZip(final String hoerernummer, final String titelnummer, final Path daisyDirectory) {
        final long startZip = System.nanoTime();
        final InputStream zipInputStream;
        try (final Stream<Path> paths = Files.list(daisyDirectory)) {
            final List<Path> files = paths.sorted().collect(Collectors.toUnmodifiableList());
            LOGGER.debug("Hörer {} Erstelle DAISY Hörbuch {} mit {} Dateien", hoerernummer, titelnummer, files.size());
            zipInputStream = zip.zipAsStream(files);
            final Path zipFile = temporaryDirectory.resolve(UUID.randomUUID() + ".zip");
            final OutputStream outputStream = Files.newOutputStream(zipFile);
            final long numBytes = zipInputStream.transferTo(outputStream);
            final long stopZip = System.nanoTime();
            LOGGER.info("Hörer {} DAISY Hörbuch {} mit {} Bytes unter {} in {} s erstellt", hoerernummer, titelnummer,
                    numBytes, zipFile, (stopZip - startZip) / 1_000_000L / 1_000L);
            zipInputStream.close();
            outputStream.close();
            return zipFile;
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

}
