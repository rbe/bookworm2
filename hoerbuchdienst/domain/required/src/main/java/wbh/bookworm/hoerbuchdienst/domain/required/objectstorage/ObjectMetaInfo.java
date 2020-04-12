/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.objectstorage;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class ObjectMetaInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String objectName;

    private final String contentType;

    private final String etag;

    private final long length;

    private final ZonedDateTime zonedDateTime;

    public ObjectMetaInfo(final String objectName, final String contentType, final String etag,
                          final long length, final ZonedDateTime zonedDateTime) {
        this.objectName = objectName;
        this.contentType = contentType;
        this.etag = etag;
        this.length = length;
        this.zonedDateTime = zonedDateTime;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getEtag() {
        return etag;
    }

    public long getLength() {
        return length;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ObjectMetaInfo that = (ObjectMetaInfo) o;
        return length == that.length &&
                contentType.equals(that.contentType) &&
                etag.equals(that.etag) &&
                zonedDateTime.equals(that.zonedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentType, etag, length, zonedDateTime);
    }

    @Override
    public String toString() {
        return String.format("ObjectMetaInfo{contentType='%s', etag='%s', length=%d, zonedDateTime=%s}",
                contentType, etag, length, zonedDateTime);
    }

}
