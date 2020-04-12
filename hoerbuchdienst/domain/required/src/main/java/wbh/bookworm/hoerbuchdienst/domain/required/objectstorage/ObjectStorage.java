/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.objectstorage;

import java.io.InputStream;
import java.util.List;

public interface ObjectStorage {

    List<String> listBuckets();

    List<ObjectMetaInfo> listObjects(String bucketName);

    void bucket(String bucketName);

    boolean bucketExists(String bucketName);

    void put(String bucketName, String objectStorageReference, InputStream stream, String contentType);

    ObjectMetaInfo metaInfo(String bucketName, String objectName);

    boolean objectExists(String bucketName, String objectName);

    boolean directoryExists(String bucketName, String objectName);

    InputStream asStream(String bucketName, String objectName);

    byte[] asBytes(String bucketName, String objectName);

    InputStream zip(String bucketName, String dirName);

    enum ContentType {

        APPLICATION_OCTET_STREAM("application/octet-stream"),
        TEXT_PLAIN("text/plain"),
        TEXT_HTML("text/html");

        private final String mimeType;

        ContentType(final String mimeType) {
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }

    }

}
