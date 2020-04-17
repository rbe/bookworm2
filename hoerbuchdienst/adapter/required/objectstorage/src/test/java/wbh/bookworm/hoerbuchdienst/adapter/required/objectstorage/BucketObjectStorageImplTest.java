/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;

import io.micronaut.http.MediaType;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.BucketObjectStorage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class BucketObjectStorageImplTest {

    @Inject
    @Named("minio")
    private BucketObjectStorage bucketObjectStorage;

    @Test
    void shouldPutTextFile() {
        final String objectName = String.format("%s.txt", getClass().getSimpleName());
        final InputStream inputStream = getClass().getResourceAsStream("/PutObjectTest.txt");
        bucketObjectStorage.put(objectName, inputStream, MediaType.TEXT_PLAIN);
        assertTrue(bucketObjectStorage.objectExists(objectName));
        assertArrayEquals("This is a text file\n".getBytes(),
                bucketObjectStorage.asBytes(objectName));
    }

}
