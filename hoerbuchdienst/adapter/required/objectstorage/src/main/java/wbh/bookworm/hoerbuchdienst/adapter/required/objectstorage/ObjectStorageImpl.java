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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.minio.CloseableIterator;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.MinioException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Event;
import io.minio.messages.Item;
import io.minio.messages.NotificationRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.BucketName;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectMetaInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorageEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorageNotificationListener;

public class ObjectStorageImpl implements ObjectStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageImpl.class);

    private final MinioClient minioClient;

    private final ExecutorService taskExecutors;

    public ObjectStorageImpl(final URI uri, final String accessKey, final String secretKey,
                             final ExecutorService taskExecutors) {
        try {
            minioClient = new MinioClient(uri.toURL(), accessKey, secretKey);
        } catch (final InvalidEndpointException | InvalidPortException | MalformedURLException e) {
            throw new ObjectStorageException(e);
        }
        this.taskExecutors = taskExecutors;
    }

    @Override
    public void registerNotificationListener(final BucketName bucketName,
                                             final ObjectStorageNotificationListener objectStorageNotificationListener,
                                             final S3Event event,
                                             final S3Event... events) {
        final S3Event[] s3Event1 = Arrays.copyOf(events, events.length + 1);
        s3Event1[s3Event1.length - 1] = event;
        final String[] eventsAsString = Arrays.stream(s3Event1)
                .map(S3Event::getEventName)
                .toArray(String[]::new);
        taskExecutors.submit(() -> {
            try (final CloseableIterator<Result<NotificationRecords>> iterator =
                         minioClient.listenBucketNotification(bucketName.val(), "", "", eventsAsString)) {
                while (iterator.hasNext()) {
                    final NotificationRecords records = iterator.next().get();
                    for (Event evt : records.events()) {
                        taskExecutors.submit(() -> {
                            objectStorageNotificationListener.process(new ObjectStorageEvent("?",
                                    "?", evt.region(), evt.eventTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), evt.eventType().name(),
                                    evt.userId(), evt.host(), evt.userAgent(), evt.responseElements().get("x-amz-request-id"),
                                    evt.responseElements().get("x-minio-deployment-id"),
                                    evt.bucketName(), evt.bucketArn(), URLDecoder.decode(evt.objectName(), StandardCharsets.UTF_8), evt.etag(),
                                    evt.objectVersionId(), evt.objectSize(), evt.userMetadata().get("content-encoding"),
                                    evt.userMetadata().get("content-type")));
                        });
                    }
                }
            } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                throw new ObjectStorageException(e);
            }
        });
    }

    @Override
    public List<BucketName> listBuckets() {
        try {
            return minioClient.listBuckets()
                    .stream()
                    .map(bucket -> new BucketName(bucket.name()))
                    .collect(Collectors.toUnmodifiableList());
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public List<ObjectMetaInfo> listAllObjects(final BucketName bucketName) {
        try {
            final Iterable<Result<Item>> results = minioClient.listObjects(bucketName.val());
            return objectMetaInfos(bucketName, results);
        } catch (XmlParserException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public List<ObjectMetaInfo> listObjects(final BucketName bucketName, final String prefix) {
        try {
            final Iterable<Result<Item>> results = minioClient.listObjects(bucketName.val(), prefix);
            return objectMetaInfos(bucketName, results);
        } catch (XmlParserException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public void bucket(final BucketName bucketName) {
        try {
            if (bucketExists(bucketName)) {
                LOGGER.warn("Bucket already exists.");
            } else {
                minioClient.makeBucket(bucketName.val());
                // TODO NotImplemented, message = A header you provided implies functionality that is not implemented
                // TODO minioClient.disableVersioning(bucketName);
            }
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public boolean bucketExists(final BucketName bucketName) {
        try {
            return minioClient.bucketExists(bucketName.val());
        } catch (final InvalidBucketNameException e) {
            return false;
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public void put(final BucketName bucketName, final String objectName, final InputStream stream, String contentType) {
        try {
            bucket(bucketName);
            final byte[] buf = stream.readAllBytes();
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
            final PutObjectOptions options = new PutObjectOptions(buf.length, -1);
            options.setContentType(contentType);
            // TODO SSE options.setSse(ServerSideEncryption.atRest());
            minioClient.putObject(bucketName.val(), objectName, byteArrayInputStream, options);
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public ObjectMetaInfo metaInfo(final BucketName bucketName, final String objectName) {
        try {
            final ObjectStat objectStat = minioClient.statObject(bucketName.val(), objectName);
            return new ObjectMetaInfo(bucketName.val(), objectName,
                    objectStat.contentType(), objectStat.etag(),
                    objectStat.length(), objectStat.createdTime());
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public boolean objectExists(final BucketName bucketName, final String objectName) {
        try {
            final ObjectStat objectStat = minioClient.statObject(bucketName.val(), objectName);
            return null != objectStat.etag() && !objectStat.etag().isBlank();
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public boolean directoryExists(final BucketName bucketName, String dirName) {
        try {
            final Iterator<Result<Item>> results = minioClient.listObjects(bucketName.val()).iterator();
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
    public InputStream asStream(final BucketName bucketName, final String objectName) {
        try {
            return minioClient.getObject(bucketName.val(), objectName);
        } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public byte[] asBytes(final BucketName bucketName, final String objectName) {
        try {
            return asStream(bucketName, objectName).readAllBytes();
        } catch (IOException e) {
            throw new ObjectStorageException(e);
        }
    }

    @Override
    public InputStream zip(final BucketName bucketName, final String dirName) {
        try {
            final Iterator<Result<Item>> results = minioClient.listObjects(bucketName.val(), dirName).iterator();
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

    private List<ObjectMetaInfo> objectMetaInfos(final BucketName bucketName, final Iterable<Result<Item>> results) {
        final List<ObjectMetaInfo> objectMetaInfos = new ArrayList<>();
        final Consumer<Result<Item>> resultConsumer = obj -> {
            try {
                final Item item = obj.get();
                if (null != item) {
                    objectMetaInfos.add(new ObjectMetaInfo(bucketName.val(), item.objectName(),
                            null, item.etag(),
                            item.size(), item.lastModified()));
                }
            } catch (final MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                LOGGER.error("", e);
            }
        };
        results.forEach(resultConsumer);
        return Collections.unmodifiableList(objectMetaInfos);
    }

}
