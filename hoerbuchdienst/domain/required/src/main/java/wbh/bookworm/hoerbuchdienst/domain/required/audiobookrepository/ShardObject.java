package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Objects;

import io.micronaut.core.annotation.Introspected;

@Introspected
public final class ShardObject implements Serializable {

    private static final long serialVersionUID = -1698948107854015839L;

    private String objectId;

    private long size;

    private String hashValue;

    private ShardName shardName;

    public ShardObject() {
    }

    public ShardObject(final String objectId,
                       final long size,
                       final String hashValue,
                       final ShardName shardName) {
        this.objectId = objectId;
        this.size = size;
        this.hashValue = hashValue;
        this.shardName = shardName;
    }

    public static ShardObject of(final ShardObject shardObject, final ShardName shardName) {
        return new ShardObject(shardObject.objectId,
                shardObject.size,
                shardObject.hashValue,
                shardName);
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

    public ShardName getShardName() {
        return shardName;
    }

    public void setShardName(final ShardName shardName) {
        this.shardName = shardName;
    }

    /* TODO unnötig */
    public void reshard(final ShardName shardName) {
        this.shardName = shardName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardObject that = (ShardObject) o;
        return shardName == that.shardName &&
                size == that.size &&
                objectId.equals(that.objectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shardName, objectId, size);
    }

    @Override
    public String toString() {
        return String.format("ShardObject{objectId='%s', shardName='%s'}", objectId, shardName);
    }

}
