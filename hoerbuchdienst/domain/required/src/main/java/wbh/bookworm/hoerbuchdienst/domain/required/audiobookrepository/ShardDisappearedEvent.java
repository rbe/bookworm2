package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class ShardDisappearedEvent implements Serializable {

    private final ZonedDateTime occurdOn;

    private final ShardName shardName;

    public ShardDisappearedEvent(final ShardName shardName) {
        this.shardName = shardName;
        occurdOn = ZonedDateTime.now();
    }

    public ZonedDateTime getOccurdOn() {
        return occurdOn;
    }

    public ShardName getShardName() {
        return shardName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardDisappearedEvent that = (ShardDisappearedEvent) o;
        return occurdOn.equals(that.occurdOn) &&
                shardName.equals(that.shardName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(occurdOn, shardName);
    }

    @Override
    public String toString() {
        return String.format("ShardDisappearedEvent{occurdOn=%s, shardName='%s'}", occurdOn, shardName);
    }

}
