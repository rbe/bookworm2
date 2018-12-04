/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;

import aoc.ddd.repository.DomainRespositoryComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@DomainRespositoryComponent
class AghNummernRepository /* TODO implements DomainRepository<>*/ {

    private static final Logger LOGGER = LoggerFactory.getLogger(AghNummernRepository.class);

    private final BlistaConfig blistaConfig;

    private final HoerbuchkatalogArchiv hoerbuchkatalogArchiv;

    @Autowired
    AghNummernRepository(final BlistaConfig blistaConfig,
                         final HoerbuchkatalogArchiv hoerbuchkatalogArchiv) {
        this.blistaConfig = blistaConfig;
        this.hoerbuchkatalogArchiv = hoerbuchkatalogArchiv;
    }

    Set<AghNummer> importiere() {
        final Optional<Path> fileName = hoerbuchkatalogArchiv.
                findeAktuellstenKatalog(Path.of("isofiles.zip"));
        if (fileName.isPresent()) {
            return importiere(fileName.get());
        } else {
            LOGGER.info("Keine AGH Nummern im Archiv gefunden");
            try {
                return importiere(aktualisiereArchiv());
            } catch (HoerbuchkatalogArchivException e) {
                LOGGER.error("", e);
                return Collections.emptySet();
            }
        }
    }

    private Set<AghNummer> importiere(final Path katalogZip) {
        LOGGER.info("Importiere AGH Nummern aus '{}'", katalogZip);
        final Set<AghNummer> aghNummern = new TreeSet<>(AghNummer::compareTo);
        try {
            final String pathInZip = blistaConfig.getAghNummernPathInZip();
            final List<String> neueAghNummern = DownloadHelper.extractLinesFromPathInZip(katalogZip, pathInZip);
            final List<String> aghNummernOhneErsteZeile = neueAghNummern.subList(1, neueAghNummern.size());
            aghNummernOhneErsteZeile.forEach(a -> {
                LOGGER.trace("Neue AGH Nummer: {}", a);
                aghNummern.add(new AghNummer(a));
            });
            LOGGER.info("Insgesamt {} AGH Nummern importiert", aghNummern.size());
        } catch (Exception e) {
            throw new HoerbuchkatalogArchivException(e);
        }
        return aghNummern;
    }

    Path aktualisiereArchiv() {
        final String url = blistaConfig.getKatalogRestUrl();
        LOGGER.info("Aktualisiere AGH Nummern von {}", url);
        try {
            final Path downloadPath = DownloadHelper.getAndSave(url);
            if (Files.exists(downloadPath)) {
                final Path isofilesZip = downloadPath.getParent().resolve("isofiles.zip");
                Files.move(downloadPath, isofilesZip);
                return hoerbuchkatalogArchiv.archiviereKatalog(isofilesZip);
            } else {
                final String message =
                        String.format("Datei (%s) mit AGH Nummern existiert nach Download nicht",
                                downloadPath);
                throw new HoerbuchkatalogArchivException(message);
            }
        } catch (Exception e) {
            throw new HoerbuchkatalogArchivException("Kann AGH Nummern nicht aktualisieren", e);
        }
    }

    private static final class DownloadHelper {

        private DownloadHelper() {
            throw new AssertionError();
        }

        static List<String> extractLinesFromPathInZip(final Path download, final String pathToExtract) {
            List<String> strings;
            try (FileSystem zipFileSystem = FileSystems.newFileSystem(download, null)) {
                final Path path = zipFileSystem.getPath(pathToExtract);
                strings = Files.readAllLines(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return strings;
        }

        static Path getAndSave(final String urlStr) throws IOException {
            final HttpsURLConnection httpsURLConnection = connect(urlStr);
            final Path tempFile = Files.createTempFile("blista", ".zip");
            tempFile.toFile().deleteOnExit();
            Files.copy(httpsURLConnection.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            httpsURLConnection.disconnect();
            return tempFile;
        }

        private static HttpsURLConnection connect(final String urlStr) throws IOException {
            final URL url = new URL(urlStr);
            final HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.connect();
            return urlConnection;
        }

    /*
        static Path getAndSave(final String urlStr) throws IOException {
            final Path tempFile = Files.createTempFile("blista", ".zip");
            tempFile.toFile().deleteOnExit();
            final EnumSet<StandardOpenOption> options =
                EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            final HttpsURLConnection httpsURLConnection = connect(urlStr);
            try (final ReadableByteChannel rbc = Channels.newChannel(httpsURLConnection.getInputStream());
                 final FileChannel fileChannel = (FileChannel) Files.newByteChannel(tempFile, options)) {
                final ByteBuffer byteBuffer = ByteBuffer.allocate(1440);
                while (rbc.read(byteBuffer) > -1) {
                    byteBuffer.flip();
                    fileChannel.write(byteBuffer);
                    byteBuffer.clear();
                }
            } finally {
                httpsURLConnection.disconnect();
            }
            return tempFile;
        }
    */

    }

}
