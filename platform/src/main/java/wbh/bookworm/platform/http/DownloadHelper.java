/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.http;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.List;

public final class DownloadHelper {

    private DownloadHelper() {
        throw new AssertionError();
    }

    public static List<String> extractLinesFromPathInZip(final Path download, final String pathToExtract) {
        List<String> strings;
        try (FileSystem zipFileSystem = FileSystems.newFileSystem(download, null)) {
            final Path path = zipFileSystem.getPath(pathToExtract);
            strings = Files.readAllLines(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return strings;
    }

    public static Path downloadUsingHttpsURLConnection(final String urlStr) throws IOException {
        final HttpsURLConnection httpsURLConnection = getHttpsURLConnection(urlStr);
        final Path tempFile = Files.createTempFile("blista", ".zip");
        tempFile.toFile().deleteOnExit();
        Files.copy(httpsURLConnection.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        httpsURLConnection.disconnect();
        return tempFile;
    }

    private static HttpsURLConnection getHttpsURLConnection(final String urlStr) throws IOException {
        final URL url = new URL(urlStr);
        final HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(true);
        urlConnection.connect();
        return urlConnection;
    }

    static Path downloadUsingNIO(final String urlStr) throws IOException {
        final Path tempFile = Files.createTempFile("blista", ".zip");
        tempFile.toFile().deleteOnExit();
        final EnumSet<StandardOpenOption> options = EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        final HttpsURLConnection httpsURLConnection = getHttpsURLConnection(urlStr);
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

}
