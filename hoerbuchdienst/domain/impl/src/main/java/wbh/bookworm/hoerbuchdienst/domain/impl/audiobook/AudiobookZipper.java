package wbh.bookworm.hoerbuchdienst.domain.impl.audiobook;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import io.micronaut.context.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookServiceException;
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

    Path watermarkedAudiobookAsZip(final String hoerernummer, /* TODO Mandantenspezifisch */ final String titelnummer) {
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
        final Path kapitelDirectory = audiobookDirectory.resolve(String.format("%sDAISY", titelnummer));
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
            Files.write(kapitelDirectory.resolve("cpr.txt"), watermark.getBytes(StandardCharsets.UTF_8));
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
            final Path zipFile = temporaryDirectory.resolve(UUID.randomUUID().toString());
            final OutputStream outputStream = Files.newOutputStream(zipFile);
            LOGGER.debug("Wrote {} bytes into ZIP file", zipInputStream.transferTo(outputStream));
            outputStream.close();
            LOGGER.info("Hörer {} DAISY Hörbuch {} erstellt", hoerernummer, titelnummer);
            return zipFile;
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        } finally {
            FilesUtils.cleanupTemporaryDirectory(/* TODO Mandantenspezifisch */kapitelDirectory);
            FilesUtils.cleanupTemporaryDirectory(audiobookDirectory);
        }
    }

}
