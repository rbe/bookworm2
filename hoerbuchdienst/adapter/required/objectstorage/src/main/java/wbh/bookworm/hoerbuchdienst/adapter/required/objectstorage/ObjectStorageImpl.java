/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectMetaInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

public class ObjectStorageImpl implements ObjectStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageImpl.class);

    private final MinioClient minioClient;

    public ObjectStorageImpl(final URI uri, final String accessKey, final String secretKey) {
        try {
            minioClient = new MinioClient(uri.toURL(), accessKey, secretKey);
        } catch (final InvalidEndpointException | InvalidPortException | MalformedURLException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public List<String> listBuckets() {
        try {
            return minioClient.listBuckets().stream()
                    .map(Bucket::name)
                    .collect(Collectors.toUnmodifiableList());
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public List<ObjectMetaInfo> listObjects(final String bucketName) {
        try {
            final Iterable<Result<Item>> results = minioClient.listObjects(bucketName);
            return StreamSupport.stream(results.spliterator(), false)
                    .map(obj -> {
                        try {
                            return obj.get();
                        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                            LOGGER.error("", e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .map(item -> {
                        /*try {
                            final String objectUrl = minioClient.getObjectUrl(bucketName, item.objectName());
                        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                            log.error("", e);
                            return null;
                        }*/
                        return new ObjectMetaInfo(item.objectName(), null, item.etag(),
                                item.size(), item.lastModified());
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList());
        } catch (XmlParserException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public void bucket(final String bucketName) {
        try {
            if (bucketExists(bucketName)) {
                LOGGER.warn("Bucket already exists.");
            } else {
                minioClient.makeBucket(bucketName);
                // TODO NotImplemented, message = A header you provided implies functionality that is not implemented
                // TODO minioClient.disableVersioning(bucketName);
            }
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public boolean bucketExists(final String bucketName) {
        try {
            return minioClient.bucketExists(bucketName);
        } catch (final InvalidBucketNameException e) {
            return false;
        } catch (final NoSuchAlgorithmException e) {
            throw new ObjectStorageException(e);
        } catch (final InsufficientDataException e) {
            throw new ObjectStorageException(e);
        } catch (final IOException e) {
            throw new ObjectStorageException(e);
        } catch (final InvalidKeyException e) {
            throw new ObjectStorageException(e);
        } catch (final XmlParserException e) {
            throw new ObjectStorageException(e);
        } catch (final ErrorResponseException e) {
            throw new ObjectStorageException(e);
        } catch (final InternalException e) {
            throw new ObjectStorageException(e);
        } catch (final InvalidResponseException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public void put(final String bucketName, final String objectName, final InputStream stream, String contentType) {
        try {
            bucket(bucketName);
            final byte[] buf = stream.readAllBytes();
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
            final PutObjectOptions options = new PutObjectOptions(buf.length, -1);
            options.setContentType(contentType);
            // TODO SSE options.setSse(ServerSideEncryption.atRest());
            minioClient.putObject(bucketName, objectName, byteArrayInputStream, options);
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public ObjectMetaInfo metaInfo(final String bucketName, final String objectName) {
        try {
            final ObjectStat objectStat = minioClient.statObject(bucketName, objectName);
            return new ObjectMetaInfo(objectName, objectStat.contentType(), objectStat.etag(),
                    objectStat.length(), objectStat.createdTime());
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public boolean objectExists(final String bucketName, final String objectName) {
        try {
            final ObjectStat objectStat = minioClient.statObject(bucketName, objectName);
            return null != objectStat.etag() && !objectStat.etag().isBlank();
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public boolean directoryExists(final String bucketName, String dirName) {
        try {
            final Iterator<Result<Item>> results = minioClient.listObjects(bucketName).iterator();
            boolean found = false;
            while (results.hasNext()) {
                try {
                    final Item item = results.next().get();
                    final boolean newValue = item.objectName().startsWith(dirName + "/");
                    if (newValue) {
                        found = true;
                        break;
                    }
                } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                    throw new ObjectStorageException(e);
                }
            }
            return found;
        } catch (XmlParserException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public InputStream asStream(final String bucketName, final String objectName) {
        try {
            return minioClient.getObject(bucketName, objectName);
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public byte[] asBytes(final String bucketName, final String objectName) {
        try {
            return asStream(bucketName, objectName).readAllBytes();
        } catch (IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public InputStream zip(final String bucketName, final String dirName) {
        try {
            final Iterator<Result<Item>> results = minioClient.listObjects(bucketName, dirName).iterator();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ZipOutputStream zipOutputStream = new ZipOutputStream(out);
            while (results.hasNext()) {
                final Item item = results.next().get();
                final ZipEntry zipEntry = new ZipEntry(item.objectName());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(asBytes(bucketName, item.objectName()));
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

}
