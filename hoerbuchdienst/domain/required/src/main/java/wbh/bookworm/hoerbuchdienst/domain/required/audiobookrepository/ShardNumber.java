package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.micronaut.core.annotation.Introspected;

@Introspected
public final class ShardNumber implements Serializable {

    private static final long serialVersionUID = -1351069385413170972L;

    private Integer shardNumber;

    @JsonCreator
    public ShardNumber(final Integer shardNumber) {
        Objects.requireNonNull(shardNumber);
        this.shardNumber = shardNumber;
    }

    public static ShardNumber of(int shardNumber) {
        return new ShardNumber(shardNumber);
    }

    public Integer getShardNumber() {
        return shardNumber;
    }

    public void setShardNumber(final int shardNumber) {
        this.shardNumber = shardNumber;
    }

    public int intValue() {
        return shardNumber;
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
