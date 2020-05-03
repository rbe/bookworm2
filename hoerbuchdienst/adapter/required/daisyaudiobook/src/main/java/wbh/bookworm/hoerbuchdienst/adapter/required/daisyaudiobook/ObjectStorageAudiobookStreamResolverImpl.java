/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aoc.mikrokosmos.objectstorage.api.BucketObjectStorage;
import aoc.mikrokosmos.objectstorage.api.ObjectStorageException;

class ObjectStorageAudiobookStreamResolverImpl implements AudiobookStreamResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageAudiobookStreamResolverImpl.class);

    private final BucketObjectStorage bucketObjectStorage;

    ObjectStorageAudiobookStreamResolverImpl(final BucketObjectStorage bucketObjectStorage) {
        this.bucketObjectStorage = bucketObjectStorage;
    }

    @Override
    public List<Path> listAll() {
        try {
            return bucketObjectStorage.listAll()
                    .stream()
                    .map(path -> path.getName(0))
                    .filter(path -> path.toString().endsWith("Kapitel"))
                    .distinct()
                    .collect(Collectors.toUnmodifiableList());
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException(e);
        }
    }

    @Override
    public List<Path> list(final String titelnummer) {
        try {
            return bucketObjectStorage.listObjects(titelnummer);
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException(e);
        }
    }

    @Override
    public InputStream nccHtmlStream(final String titelnummer) {
        try {
            return bucketObjectStorage.asStream(String.format("%sKapitel/ncc.html", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException(e);
        }
    }

    @Override
    public InputStream masterSmilStream(final String titelnummer) {
        try {
            return bucketObjectStorage.asStream(String.format("%sKapitel/master.smil", titelnummer));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException(e);
        }
    }

    @Override
    public InputStream trackAsStream(final String titelnummer, final String ident) {
        try {
            return bucketObjectStorage.asStream(String.format("%sKapitel/%s", titelnummer, ident));
        } catch (ObjectStorageException e) {
            throw new AudiobookStreamResolverException(e);
        }
    }

}
