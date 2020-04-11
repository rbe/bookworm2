/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.micronaut.http.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import wbh.bookworm.hoerbuchdienst.domain.required.ObjectStorage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class ObjectStorageImplTest {

    public static final String BUCKET_NAME = "rogers";

    private static ObjectStorage objectStorage;

    @BeforeAll
    public static void beforeAll() {
        final String accessKey = "accesskey";
        final String secretKey = "secretkey";
        final URI uri = URI.create("https://storage.medienhof9.rootaid.de");
        objectStorage = new ObjectStorageImpl(uri, accessKey, secretKey);
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
    }

    @Test
    void shouldGetObject() {
        final String objectName = "PutObjectTest.txt";
        assertTrue(objectStorage.objectExists(BUCKET_NAME, objectName));
        try (final InputStream inputStream = objectStorage.asStream(BUCKET_NAME, objectName)) {
            final byte[] bytes = inputStream.readAllBytes();
            log.info("{}", new String(bytes, StandardCharsets.UTF_8));
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
        objectStorage.put(BUCKET_NAME, "dir/dummy.pdf", inputStream2, MediaType.TEXT_PLAIN);
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
