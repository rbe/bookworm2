/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.micronaut.http.MediaType;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.BucketName;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectMetaInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage.S3Event.S3_OBJECT_ACCESSED_HEAD;

@MicronautTest
class ObjectStorageImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageImplTest.class);

    public static final BucketName BUCKET_NAME = new BucketName("rogers");

    @Inject
    @Named("minio")
    private ObjectStorage objectStorage;

    @Test
    void shouldListenForBucketNotifications() {
        objectStorage.registerNotificationListener(BUCKET_NAME, objectStorageEvent -> {
            LOGGER.info("{}", objectStorageEvent);
            assertEquals(BUCKET_NAME, objectStorageEvent.getBucketName());
            assertNotNull(objectStorageEvent.getBucketArn());
            assertNotNull(objectStorageEvent.getObjectName());
            assertNotNull(objectStorageEvent.getEventName());
        }, S3_OBJECT_ACCESSED_HEAD);
        objectStorage.objectExists(BUCKET_NAME, "PutObjectTest.txt");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldListExistingBuckets() {
        final List<BucketName> buckets = objectStorage.listBuckets();
        assertNotNull(buckets);
        LOGGER.info("{}", buckets);
    }

    @Test
    void shouldListExistingObjects() {
        final List<ObjectMetaInfo> objectMetaInfos = objectStorage.listAllObjects(BUCKET_NAME);
        assertNotNull(objectMetaInfos);
        for (ObjectMetaInfo objectMetaInfo : objectMetaInfos) {
            LOGGER.info("{}", objectMetaInfo.getObjectName());
        }
    }

    @Test
    void shouldCreateBucket() {
        objectStorage.bucket(BUCKET_NAME);
        assertTrue(objectStorage.bucketExists(BUCKET_NAME));
    }

    @Test
    void shouldSeeExistingBucket() {
        assertTrue(objectStorage.bucketExists(BUCKET_NAME));
    }

    @Test
    void shouldPutObject() {
        final String objectName = "PutObjectTest.txt";
        final InputStream inputStream = getClass().getResourceAsStream("/PutObjectTest.txt");
        objectStorage.put(BUCKET_NAME, objectName, inputStream, MediaType.TEXT_PLAIN);
        assertTrue(objectStorage.objectExists(BUCKET_NAME, objectName));
        assertArrayEquals("This is a text file\n".getBytes(), objectStorage.asBytes(BUCKET_NAME, "/PutObjectTest.txt"));
    }

    @Test
    void shouldGetObject() {
        final String objectName = "PutObjectTest.txt";
        assertTrue(objectStorage.objectExists(BUCKET_NAME, objectName));
        try (final InputStream inputStream = objectStorage.asStream(BUCKET_NAME, objectName)) {
            final byte[] bytes = inputStream.readAllBytes();
            // TODO Assert content equals
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void shouldPutObjectInBucket() {
        final String objectName = "PutObjectTest.txt";
        final InputStream inputStream = getClass().getResourceAsStream("/PutObjectTest.txt");
        objectStorage.put(BUCKET_NAME, objectName, inputStream, MediaType.TEXT_PLAIN);
        assertTrue(objectStorage.objectExists(BUCKET_NAME, objectName));
    }

    @Test
    void shouldPutObjectsInDirectory() {
        final InputStream inputStream1 = getClass().getResourceAsStream("/PutObjectTest.txt");
        objectStorage.put(BUCKET_NAME, "dir/PutObjectTest.txt", inputStream1, MediaType.TEXT_PLAIN);
        assertTrue(objectStorage.objectExists(BUCKET_NAME, "dir/PutObjectTest.txt"));
        final InputStream inputStream2 = getClass().getResourceAsStream("/dummy.pdf");
        objectStorage.put(BUCKET_NAME, "dir/dummy.pdf", inputStream2, /* TODO ObjectStorage.Types */"application/pdf");
        assertTrue(objectStorage.objectExists(BUCKET_NAME, "dir/dummy.pdf"));
    }

    @Test
    void shouldSeeDirectory() {
        final String dirName = "dir";
        assertTrue(objectStorage.directoryExists(BUCKET_NAME, dirName));
    }

    @Test
    void shouldGetDirectoryAsZip() {
        final String dirName = "dir";
        assertTrue(objectStorage.directoryExists(BUCKET_NAME, dirName));
        try (final InputStream inputStream = objectStorage.zip(BUCKET_NAME, dirName)) {
            final byte[] bytes = inputStream.readAllBytes();
            assertTrue(bytes.length > 0);
            Files.write(Path.of("target/Dir.zip"), bytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
