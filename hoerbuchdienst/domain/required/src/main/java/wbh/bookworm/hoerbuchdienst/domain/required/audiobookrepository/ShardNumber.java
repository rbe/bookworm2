package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ShardNumber extends Number {

    private static final long serialVersionUID = -1351069385413170972L;

    private static final Map<Integer, ShardNumber> SHARD_NUMBER_CACHE = new ConcurrentHashMap<>();

    private static final transient Object LOCK = new Object();

    private final int shardNumber;

    private ShardNumber(int shardNumber) {
        this.shardNumber = shardNumber;
    }

    public static ShardNumber of(int shardNumber) {
        synchronized (LOCK) {
            SHARD_NUMBER_CACHE.putIfAbsent(shardNumber, new ShardNumber(shardNumber));
            return SHARD_NUMBER_CACHE.get(shardNumber);
        }
    }

    @Override
    public int intValue() {
        return shardNumber;
    }

    @Override
    public long longValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float floatValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double doubleValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardNumber that = (ShardNumber) o;
        return shardNumber == that.shardNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shardNumber);
    }

    @Override
    public String toString() {
        return String.format("%d", shardNumber);
    }

}
