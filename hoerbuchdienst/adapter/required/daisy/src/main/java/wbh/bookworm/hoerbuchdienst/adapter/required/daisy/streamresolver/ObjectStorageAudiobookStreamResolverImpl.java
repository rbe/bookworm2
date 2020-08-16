/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
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

    private Path temporaryDirectory;

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
            return bucketObjectStorage.listAllObjects()
                    .stream()
                    .map(path -> path.getName(0))
                    // TODO "Kapitel" Suffix ist mandantenspezifisch
                    .filter(path -> path.toString().endsWith("Kapitel"))
                    .distinct()
                    .collect(Collectors.toUnmodifiableList());
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public List<ObjectMetaInfo> allObjectsMetaInfo() {
        return bucketObjectStorage.allObjectsMetaInfo();
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
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            return bucketObjectStorage.asStream(String.format("%sKapitel/ncc.html", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream masterSmilStream(/* TODO Mandantenspezifisch */final String titelnummer) {
        try {
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            return bucketObjectStorage.asStream(String.format("%sKapitel/master.smil", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream trackAsStream(/* TODO Mandantenspezifisch */final String titelnummer, final String ident) {
        try {
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            return bucketObjectStorage.asStream(String.format("%sKapitel/%s", titelnummer, ident));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream zipAsStream(/* TODO Mandantenspezifisch */final String titelnummer) {
        try {
            return bucketObjectStorage.asZip(/* TODO Mandantenspezifisch */String.format("%sKapitel", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public /* TODO BucketHashValue */String putZip(final InputStream inputStream, /* TODO Mandantenspezifisch */final String titelnummer) {
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
        try {
            final Path[] files = Files.walk(unpackDirectory)
                    .filter(Files::isRegularFile)
                    .toArray(Path[]::new);
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
        final List<ObjectStorage.RemoveResult> removedPaths = bucketObjectStorage.removeObjects(/* TODO Mandantenspezifisch */String.format("%sKapitel", titelnummer));
        LOGGER.info("Removed audiobook {} with its contents {}", titelnummer, removedPaths);
    }

    private void putFile(final Path path, /* TODO Mandantenspezifisch */final String titelnummer) {
        LOGGER.debug("Putting file {} into audiobook {}", path.toAbsolutePath(), titelnummer);
        try {
            final String objectName = String.format("%sKapitel/%s", titelnummer, path.getFileName());
            bucketObjectStorage.put(objectName, Files.newInputStream(path), APPLICATION_ZIP);
            LOGGER.debug("Successfully put file {} into audiobook {}", path, titelnummer);
        } catch (IOException e) {
            LOGGER.error("Cannot put object {} into audiobook {}", path, titelnummer);
        }
    }

}
