/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.objectstorage;

import java.io.Serializable;

/** TODO Most of this data should be moved to @see ObjectStorageEvent. */
public final class ObjectStorageEvent implements Serializable {

    private static final long serialVersionUID = -1L;

    /** Event version, "2.0", event.eventVersion */
    private final String eventVersion;

    /** Event source, e.g. "minio:s3", event.eventSource */
    private final String eventSource;

    /** E.g. us-east-1, event.awsRegion */
    private final String awsRegion;

    /** Time of event, event.eventTime */
    private final String eventTime;

    /** Event name, e.g. s3:ObjectAccessed:Head, event.eventName */
    private final String eventName;

    /** Access key of user, event.userIdentity.principalId */
    private final String userPrincipalId;

    /** Originating IP address (requestParameters[sourceIPAddress] or event.source.host/port) */
    private final String sourceIpAddress;

    /** User Agent, event.source.userAgent */
    private final String userAgent;

    /** Request ID, requestParemeters[X-Amz-Request-Id] */
    private final String requestId;

    /** X-Minio-Deployment-Id */
    private final String deploymentId;

    /** Bucket name */
    private final String bucketName;

    /** arn:aws:s3:::rogers */
    private final String bucketArn;

    /** Object name, relative to bucket */
    private final String objectName;

    /** E-Tag */
    private final String objectEtag;

    /** Object's version */
    private final String objectVersionId;

    /** Object's size */
    private final long objectSize;

    /** Content-Encoding */
    private final String objectContentEncoding;

    /** Content-Type */
    private final String objectContentType;

    public ObjectStorageEvent(final String eventVersion, final String eventSource, final String awsRegion, final String eventTime, final String eventName, final String userPrincipalId, final String sourceIpAddress, final String userAgent, final String requestId, final String deploymentId, final String bucketName, final String bucketArn, final String objectName, final String objectEtag, final String objectVersionId, final long objectSize, final String objectContentEncoding, final String objectContentType) {
        this.eventVersion = eventVersion;
        this.eventSource = eventSource;
        this.awsRegion = awsRegion;
        this.eventTime = eventTime;
        this.eventName = eventName;
        this.userPrincipalId = userPrincipalId;
        this.sourceIpAddress = sourceIpAddress;
        this.userAgent = userAgent;
        this.requestId = requestId;
        this.deploymentId = deploymentId;
        this.bucketName = bucketName;
        this.bucketArn = bucketArn;
        this.objectName = objectName;
        this.objectEtag = objectEtag;
        this.objectVersionId = objectVersionId;
        this.objectSize = objectSize;
        this.objectContentEncoding = objectContentEncoding;
        this.objectContentType = objectContentType;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public String getEventSource() {
        return eventSource;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getUserPrincipalId() {
        return userPrincipalId;
    }

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getBucketArn() {
        return bucketArn;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectEtag() {
        return objectEtag;
    }

    public String getObjectVersionId() {
        return objectVersionId;
    }

    public long getObjectSize() {
        return objectSize;
    }

    public String getObjectContentEncoding() {
        return objectContentEncoding;
    }

    public String getObjectContentType() {
        return objectContentType;
    }

    @Override
    public String toString() {
        return String.format("ObjectStorageEvent{eventVersion='%s', eventSource='%s', awsRegion='%s', eventTime='%s', eventName='%s', userPrincipalId='%s', sourceIpAddress='%s', userAgent='%s', requestId='%s', deploymentId='%s', bucketName='%s', bucketArn='%s', objectName='%s', objectEtag='%s', objectVersionId='%s', objectSize=%d, objectContentEncoding='%s', objectContentType='%s'}",
                eventVersion, eventSource, awsRegion, eventTime, eventName, userPrincipalId, sourceIpAddress, userAgent, requestId, deploymentId, bucketName, bucketArn, objectName, objectEtag, objectVersionId, objectSize, objectContentEncoding, objectContentType);
    }

}
