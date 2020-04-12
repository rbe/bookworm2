/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import javax.inject.Inject;
import java.io.InputStream;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.BucketObjectStorage;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

public class BucketObjectStorageImpl implements BucketObjectStorage {

    private final ObjectStorage objectStorage;

    private final String bucketName;

    @Inject
    public BucketObjectStorageImpl(final ObjectStorage objectStorage, final String bucketName) {
        this.objectStorage = objectStorage;
        this.bucketName = bucketName;
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
