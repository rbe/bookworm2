package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Objects;

import io.micronaut.core.annotation.Introspected;

@Introspected
public final class ShardObject implements Serializable {

    private static final long serialVersionUID = -1698948107854015839L;

    private String id;

    private long size;

    private String hashValue;

    private ShardNumber shardNumber;

    public ShardObject() {
    }

    public ShardObject(final String id,
                       final long size,
                       final String hashValue,
                       final int shardNumber) {
        this.id = id;
        this.size = size;
        this.hashValue = hashValue;
        this.shardNumber = ShardNumber.of(shardNumber);
    }

    public static ShardObject of(final ShardObject shardObject, final ShardNumber shardNumber) {
        return new ShardObject(shardObject.getId(),
                shardObject.getSize(),
                shardObject.getHashValue(),
                shardNumber.intValue());
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public ShardNumber getShardNumber() {
        return shardNumber;
    }

    public void setShardNumber(final ShardNumber shardNumber) {
        this.shardNumber = shardNumber;
    }

    /* TODO unnötig */
    public void reshard(final ShardNumber shardNumber) {
        this.shardNumber = shardNumber;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardObject that = (ShardObject) o;
        return shardNumber == that.shardNumber &&
                size == that.size &&
                id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shardNumber, id, size);
    }

    @Override
    public String toString() {
        return String.format("ShardObject{shardNumber='%s', id='%s'}", shardNumber, id);
    }

}
