/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aoc.mikrokosmos.io.fs.FilesUtils;
import aoc.mikrokosmos.io.zip.Zip;
import aoc.mikrokosmos.objectstorage.api.BucketObjectStorage;
import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;
import aoc.mikrokosmos.objectstorage.api.ObjectStorage;
import aoc.mikrokosmos.objectstorage.api.ObjectStorageException;

class ObjectStorageAudiobookStreamResolverImpl implements AudiobookStreamResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageAudiobookStreamResolverImpl.class);

    private static final String APPLICATION_ZIP = "application/zip";

    private final BucketObjectStorage bucketObjectStorage;

    private final Zip zip;

    private final Path temporaryDirectory;

    ObjectStorageAudiobookStreamResolverImpl(final BucketObjectStorage bucketObjectStorage,
                                             final Zip zip,
                                             final Path temporaryDirectory) {
        this.bucketObjectStorage = bucketObjectStorage;
        this.zip = zip;
        this.temporaryDirectory = temporaryDirectory;
    }

    @PostConstruct
    private void postConstruct() {
        LOGGER.debug("This is {}", this);
    }

    @Override
    public List<Path> listAll() {
        try {
            return bucketObjectStorage.listObjects()
                    .stream()
                    .map(path -> path.getName(0))
                    .filter(path -> path.toString().endsWith("DAISY"))
                    .distinct()
                    .collect(Collectors.toUnmodifiableList());
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public List<ObjectMetaInfo> objectsMetaInfo() {
        return bucketObjectStorage.objectsMetaInfo();
    }

    @Override
    public List<Path> list(/* TODO Mandantenspezifisch */final String titelnummer) {
        try {
            return bucketObjectStorage.listAllObjects(titelnummer);
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream nccHtmlStream(/* TODO Mandantenspezifisch */final String titelnummer) {
        try {
            return bucketObjectStorage.asStream(String.format("%sDAISY/ncc.html", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream masterSmilStream(/* TODO Mandantenspezifisch */final String titelnummer) {
        try {
            return bucketObjectStorage.asStream(String.format("%sDAISY/master.smil", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream trackAsStream(/* TODO Mandantenspezifisch */final String titelnummer, final String ident) {
        try {
            return bucketObjectStorage.asStream(String.format("%sDAISY/%s", titelnummer, ident));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream zipAsStream(/* TODO Mandantenspezifisch */final String titelnummer) {
        try {
            return bucketObjectStorage.asZip(String.format("%sDAISY", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public String putZip(final byte[] bytes, /* TODO Mandantenspezifisch */final String titelnummer) {
        Objects.requireNonNull(bytes);
        Objects.requireNonNull(titelnummer);
        LOGGER.debug("Received ZIP with {} bytes", bytes.length);
        return putZip(new ByteArrayInputStream(bytes), titelnummer);
    }

    @Override
    public /* TODO BucketHashValue */String putZip(final InputStream inputStream, /* TODO Mandantenspezifisch */final String titelnummer) {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(titelnummer);
        LOGGER.info("Putting ZIP archive for object '{}' into bucket {}", titelnummer, bucketObjectStorage.getBucketName());
        // unpack zip and put every file into object storage
        final Path unpackDirectory = temporaryDirectory.resolve(titelnummer + "_zip");
        try {
            LOGGER.debug("Creating directory {}", unpackDirectory);
            Files.createDirectories(unpackDirectory);
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
        LOGGER.debug("Unpacking ZIP archive for object '{}'", titelnummer);
        zip.unzip(new ZipInputStream(inputStream), unpackDirectory);
        try (final Stream<Path> pathStream = Files.walk(unpackDirectory)
                .filter(Files::isRegularFile)) {
            final Path[] files = pathStream.toArray(Path[]::new);
            for (final Path file : files) {
                LOGGER.debug("Putting {} files into audiobook {}", file, titelnummer);
                putFile(file, titelnummer);
            }
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
        LOGGER.info("Successfully put object '{}' into bucket {}", titelnummer, bucketObjectStorage.getBucketName());
        FilesUtils.cleanupTemporaryDirectory(unpackDirectory);
        return bucketObjectStorage.hashValueForPrefix(titelnummer);
    }

    @Override
    public void removeZip(/* TODO Mandantenspezifisch */final String titelnummer) {
        final List<ObjectStorage.RemoveResult> removedPaths = bucketObjectStorage.removeObjects(String.format("%sDAISY", titelnummer));
        LOGGER.info("Removed audiobook {} with its contents {}", titelnummer, removedPaths);
    }

    @Override
    public Path mp3ToTempDirectory(/* TODO Mandantenspezifisch */final String titelnummer, final Path tempDirectory) {
        try {
            Files.createDirectories(tempDirectory);
        } catch (IOException e) {
            final String message = String.format("Cannot create temporary directory %s", tempDirectory.toAbsolutePath());
            throw new AudiobookStreamResolverException(message, e);
        }
        final List<Path> allObjects = bucketObjectStorage.listAllObjects(titelnummer);
        for (Path object : allObjects) {
            final Path tempMp3 = tempDirectory.resolve(object.getFileName());
            final long start = System.nanoTime();
            try (final OutputStream outputStream = Files.newOutputStream(tempMp3)) {
                bucketObjectStorage.asStream(object.toString())
                        .transferTo(outputStream);
                final long stop = System.nanoTime();
                LOGGER.debug("Copyied {} to {} in {} ms", object, tempMp3, (stop - start) / 1_000_000L);
            } catch (IOException e) {
                throw new AudiobookStreamResolverException("", e);
            }
        }
        return tempDirectory;
    }

    private void putFile(final Path path, /* TODO Mandantenspezifisch */final String titelnummer) {
        LOGGER.debug("Putting file {} into audiobook {}", path.toAbsolutePath(), titelnummer);
        try {
            final String objectName = String.format("%sDAISY/%s", titelnummer, path.getFileName());
            bucketObjectStorage.put(objectName, Files.newInputStream(path), APPLICATION_ZIP);
            LOGGER.debug("Successfully put file {} into audiobook {}", path, titelnummer);
        } catch (IOException e) {
            LOGGER.error("Cannot put object {} into audiobook {}", path, titelnummer);
        }
    }

}
