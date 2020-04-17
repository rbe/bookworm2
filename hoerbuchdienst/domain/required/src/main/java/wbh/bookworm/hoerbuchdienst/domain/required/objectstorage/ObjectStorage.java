/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.objectstorage;

import java.io.InputStream;
import java.util.List;

/**
 * I provide access to an object storage (S3-compliant, like MinIO).
 */
public interface ObjectStorage {

    /**
     * I run a Bucket notification listener in a separate thread and
     * execute callback in their own threads.
     */
    void registerNotificationListener(BucketName bucketName,
                                      ObjectStorageNotificationListener objectStorageNotificationListener,
                                      S3Event event, S3Event... events);

    List<BucketName> listBuckets();

    List<ObjectMetaInfo> listAllObjects(BucketName bucketName);

    List<ObjectMetaInfo> listObjects(BucketName bucketName, String prefix);

    void bucket(BucketName bucketName);

    boolean bucketExists(BucketName bucketName);

    /**
     * @param contentType See
     * @see "io.micronaut.http.MediaType"
     */
    void put(BucketName bucketName, String objectName, InputStream stream, String contentType);

    ObjectMetaInfo metaInfo(BucketName bucketName, String objectName);

    boolean objectExists(BucketName bucketName, String objectName);

    boolean directoryExists(BucketName bucketName, String objectName);

    InputStream asStream(BucketName bucketName, String objectName);

    byte[] asBytes(BucketName bucketName, String objectName);

    InputStream zip(BucketName bucketName, String dirName);

    /**
     * See <a href="https://docs.min.io/docs/minio-bucket-notification-guide.html">MinIO Bucket Notification Guide</a>
     * <ul>
     * <li>s3:ObjectCreated:*
     *   <ul>
     *     <li>s3:ObjectCreated:Put</li>
     *     <li>s3:ObjectCreated:CompleteMultipartUpload</li>
     *     <li>s3:ObjectCreated:Post</li>
     *     <li>s3:ObjectCreated:Copy</li>
     *   </ul>
     * </li>
     * <li>s3:ObjectAccessed:*
     *   <ul>
     *     <li>s3:ObjectAccessed:Head</li>
     *     <li>s3:ObjectAccessed:Get</li>
     *   </ul>
     * </li>
     * <li>s3:ObjectRemoved:Delete</li>
     * </ul>
     */
    enum S3Event {

        S3_OBJECT_CREATED_ALL("s3:ObjectCreated:*"),
        S3_OBJECT_CREATED_PUT("s3:ObjectCreated:Put"),
        S3_OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD("s3:ObjectCreated:CompleteMultipartUpload"),
        S3_OBJECT_CREATED_POST("s3:ObjectCreated:Post"),
        S3_OBJECT_CREATED_COPY("s3:ObjectCreated:Copy"),
        S3_OBJECT_ACCESSED_ALL("s3:ObjectAccessed:*"),
        S3_OBJECT_ACCESSED_HEAD("s3:ObjectAccessed:Head"),
        S3_OBJECT_ACCESSED_GET("s3:ObjectAccessed:Get"),
        S3_OBJECT_REMOVED("s3:ObjectRemoved:Delete");

        private final String eventName;

        S3Event(final String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return eventName;
        }

    }

    /**
     * @see io.micronaut.http.MediaType
     */
    enum ContentType {

        APPLICATION_OCTET_STREAM("application/octet-stream"),
        APPLICATION_PDF("application/pdf"),
        TEXT_PLAIN("text/plain"),
        TEXT_HTML("text/html");

        private final String contentType;

        ContentType(final String contentType) {
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }

    }

}
