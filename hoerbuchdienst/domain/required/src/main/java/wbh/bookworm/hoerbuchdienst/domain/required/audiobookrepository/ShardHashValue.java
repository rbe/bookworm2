package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public final class ShardHashValue implements Serializable, Comparable<ShardHashValue> {

    private static final long serialVersionUID = -3163831401609204333L;

    private final String hashValue;

    public ShardHashValue(final String hashValue) {
        Objects.requireNonNull(hashValue);
        if (hashValue.isBlank()) {
            throw new IllegalArgumentException("Hash value is empty");
        }
        this.hashValue = hashValue;
    }

    public String getHashValue() {
        return hashValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardHashValue that = (ShardHashValue) o;
        return hashValue.equals(that.hashValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashValue);
    }

    @Override
    public String toString() {
        return hashValue;
    }

    @Override
    public int compareTo(final ShardHashValue o) {
        return Comparator.comparing(ShardHashValue::getHashValue)
                .compare(this, o);
    }

}
