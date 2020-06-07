package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public final class DataHeartbeat implements Serializable {

    private static final long serialVersionUID = 2963762098263689022L;

    private final Instant pointInTime;

    private final ShardNumber shardNumber;

    private final String shardname;

    private final long totalBytes;

    private final long usedBytes;

    private final double usageInPercent;

    private final List<ShardObject> shardObjects;

    @JsonCreator
    public DataHeartbeat(@JsonProperty("pointInTime") final Instant pointInTime,
                         @JsonProperty("shardname") final String shardname,
                         @JsonProperty("totalBytes") final long totalBytes,
                         @JsonProperty("usedBytes") final long usedBytes,
                         @JsonProperty("shardNumber") final ShardNumber shardNumber,
                         @JsonProperty("shardObjects") final List<ShardObject> shardObjects) {
        this.pointInTime = pointInTime;
        this.shardname = shardname;
        this.totalBytes = totalBytes;
        this.usedBytes = usedBytes;
        usageInPercent = BigDecimal.valueOf(((double) usedBytes / (double) totalBytes) * 100.0d)
                .setScale(2, RoundingMode.CEILING)
                .doubleValue();
        this.shardNumber = shardNumber;
        this.shardObjects = shardObjects;
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

    public long getTotalBytes() {
        return totalBytes;
    }

    public long getUsedBytes() {
        return usedBytes;
    }

    public double getUsageInPercent() {
        return usageInPercent;
    }

    public List<ShardObject> getShardObjects() {
        return shardObjects;
    }

    @Override
    public String toString() {
        return String.format("DataHeartbeat{pointInTime='%s', shardname='%s', totalBytes=%d, usedBytes=%d, usageInPercent=%.2f, shardObjects='%s'}",
                pointInTime, shardname, totalBytes, usedBytes, usageInPercent, shardObjects);
    }

}
