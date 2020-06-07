package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class ShardAppearedEvent implements Serializable {

    private final ZonedDateTime occurdOn;

    private final String shardName;

    public ShardAppearedEvent(final String shardName) {
        this.shardName = shardName;
        occurdOn = ZonedDateTime.now();
    }

    public String getShardName() {
        return shardName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardAppearedEvent that = (ShardAppearedEvent) o;
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
