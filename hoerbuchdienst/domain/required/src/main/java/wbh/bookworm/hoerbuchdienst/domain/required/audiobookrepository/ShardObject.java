package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Objects;

import io.micronaut.core.annotation.Introspected;

/**
 * A single object in shard's storage
 */
@Introspected
public final class ShardObject implements Serializable {

    private static final long serialVersionUID = -1698948107854015839L;

    private String objectId;

    private long size;

    private String hashValue;

    public ShardObject() {
    }

    public ShardObject(final String objectId,
                       final long size,
                       final String hashValue) {
        this.objectId = objectId;
        this.size = size;
        this.hashValue = hashValue;
    }

    public static ShardObject of(final ShardObject shardObject) {
        return new ShardObject(shardObject.objectId,
                shardObject.size,
                shardObject.hashValue);
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(final String hashValue) {
        this.hashValue = hashValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardObject that = (ShardObject) o;
        return size == that.size &&
                objectId.equals(that.objectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, size);
    }

    @Override
    public String toString() {
        return String.format("ShardObject{objectId='%s',  size=%d}", objectId, size);
    }

}
