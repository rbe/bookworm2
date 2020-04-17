/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.BucketName;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.BucketObjectStorage;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectMetaInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

public class BucketObjectStorageImpl implements BucketObjectStorage {

    private final ObjectStorage objectStorage;

    private final BucketName bucketName;

    @Inject
    public BucketObjectStorageImpl(final ObjectStorage objectStorage, final BucketName bucketName) {
        this.objectStorage = objectStorage;
        this.bucketName = bucketName;
    }

    @Override
    public List<Path> listAll() {
        return objectStorage.listAllObjects(bucketName)
                .stream()
                .map(ObjectMetaInfo::getObjectName)
                .map(Path::of)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Path> listObjects(final String prefix) {
        return objectStorage.listObjects(bucketName, prefix)
                .stream()
                .map(ObjectMetaInfo::getObjectName)
                .map(Path::of)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void put(final String objectName, final InputStream stream, final String contentType) {
        objectStorage.put(bucketName, objectName, stream, contentType);
    }

    @Override
    public boolean objectExists(final String objectName) {
        return objectStorage.objectExists(bucketName, objectName);
    }

    @Override
    public InputStream asStream(final String objectName) {
        return objectStorage.asStream(bucketName, objectName);
    }

    @Override
    public byte[] asBytes(final String objectName) {
        return objectStorage.asBytes(bucketName, objectName);
    }

    @Override
    public InputStream zip(final String dirName) {
        return objectStorage.zip(bucketName, dirName);
    }

}
