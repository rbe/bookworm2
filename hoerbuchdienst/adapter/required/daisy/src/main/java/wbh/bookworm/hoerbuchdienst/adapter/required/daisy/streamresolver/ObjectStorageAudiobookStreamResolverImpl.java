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
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aoc.mikrokosmos.io.fs.FilesUtils;
import aoc.mikrokosmos.io.zip.Zip;
import aoc.mikrokosmos.objectstorage.api.BucketObjectStorage;
import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;
import aoc.mikrokosmos.objectstorage.api.ObjectStorageException;

class ObjectStorageAudiobookStreamResolverImpl implements AudiobookStreamResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageAudiobookStreamResolverImpl.class);

    private static final String APPLICATION_ZIP = "application/zip";

    private final BucketObjectStorage bucketObjectStorage;

    private final Zip zip;

    @Property(name = ResolverConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    ObjectStorageAudiobookStreamResolverImpl(final BucketObjectStorage bucketObjectStorage,
                                             final Zip zip) {
        this.bucketObjectStorage = bucketObjectStorage;
        this.zip = zip;
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
    public List<Path> list(final String titelnummer) {
        try {
            return bucketObjectStorage.listAllObjects(titelnummer);
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream nccHtmlStream(final String titelnummer) {
        try {
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            return bucketObjectStorage.asStream(String.format("%sKapitel/ncc.html", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream masterSmilStream(final String titelnummer) {
        try {
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            return bucketObjectStorage.asStream(String.format("%sKapitel/master.smil", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream trackAsStream(final String titelnummer, final String ident) {
        try {
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            return bucketObjectStorage.asStream(String.format("%sKapitel/%s", titelnummer, ident));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream zipAsStream(final String titelnummer) {
        try {
            return bucketObjectStorage.asZip(/* TODO Mandantenspezifisch */String.format("%sKapitel", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public String putZip(final InputStream inputStream, final String titelnummer) {
        LOGGER.debug("Unpack zip archive for object '{}'", titelnummer);
        // unpack zip and put every file into object storage
        final Path unpackDirectory = temporaryDirectory.resolve(titelnummer + "_zip");
        LOGGER.info("Unpacking ZIP archive of audiobook {}", titelnummer);
        zip.unzip(new ZipInputStream(inputStream), unpackDirectory);
        LOGGER.debug("Putting audiobook {} into object storage", titelnummer);
        try (final Stream<Path> stream = Files.walk(unpackDirectory)
                .filter(Files::isRegularFile)) {
            stream.forEach(path -> putFile(path, titelnummer));
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
        FilesUtils.cleanupTemporaryDirectory(unpackDirectory);
        return bucketObjectStorage.hashValueForPrefix(titelnummer);
    }

    private void putFile(final Path path, /* TODO Mandantenspezifisch */final String titelnummer) {
        try {
            final String objectName = String.format("%sKapitel/%s", titelnummer, path.getFileName());
            bucketObjectStorage.put(objectName, Files.newInputStream(path), APPLICATION_ZIP);
            LOGGER.info("Sucessfully put object '{}' into object storage", path);
        } catch (IOException e) {
            LOGGER.error("Cannot put object '{}' into object storage", path);
        }
    }

}
