package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public final class Heartbeat implements Serializable {

    private static final long serialVersionUID = 2963762098263689022L;

    private final Instant pointInTime;

    private final ShardNumber shardNumber;

    private final String shardname;

    @JsonCreator
    public Heartbeat(@JsonProperty("pointInTime") final Instant pointInTime,
                     @JsonProperty("shardname") final String shardname,
                     @JsonProperty("shardNumber") final ShardNumber shardNumber) {
        this.pointInTime = pointInTime;
        this.shardname = shardname;
        this.shardNumber = shardNumber;
    }

    public Instant getPointInTime() {
        return pointInTime;
    }

    @JsonIgnore
    public ZonedDateTime pointInTime() {
        return pointInTime.atZone(ZoneId.systemDefault());
    }

    public ShardNumber getShardNumber() {
        return shardNumber;
    }

    public String getShardname() {
        return shardname;
    }

    @Override
    public String toString() {
        return String.format("Heartbeat{pointInTime='%s', shardname='%s'}", pointInTime, shardname);
    }

}
