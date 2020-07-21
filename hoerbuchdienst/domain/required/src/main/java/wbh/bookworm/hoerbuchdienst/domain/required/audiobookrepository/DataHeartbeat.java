package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public final class DataHeartbeat implements Serializable {

    private static final long serialVersionUID = 2963762098263689022L;

    private final Instant pointInTime;

    private final ShardName shardName;

    private final long totalBytes;

    private final long usedBytes;

    private final double usageInPercent;

    private final List<ShardObject> shardObjects;

    @JsonCreator
    public DataHeartbeat(@JsonProperty("pointInTime") final Instant pointInTime,
                         @JsonProperty("shardname") final ShardName shardName,
                         @JsonProperty("totalBytes") final long totalBytes,
                         @JsonProperty("usedBytes") final long usedBytes,
                         @JsonProperty("shardObjects") final List<ShardObject> shardObjects) {
        this.pointInTime = pointInTime;
        this.shardName = shardName;
        this.totalBytes = totalBytes;
        this.usedBytes = usedBytes;
        usageInPercent = BigDecimal.valueOf(((double) usedBytes / (double) totalBytes) * 100.0d)
                .setScale(2, RoundingMode.CEILING)
                .doubleValue();
        this.shardObjects = shardObjects;
    }

    public Instant getPointInTime() {
        return pointInTime;
    }

    @JsonIgnore
    public ZonedDateTime pointInTime() {
        return pointInTime.atZone(ZoneId.systemDefault());
    }

    public ShardName getShardName() {
        return shardName;
    }

    public String getShardname() {
        return shardName.getHostName();
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
        return null != shardObjects
                ? Collections.unmodifiableList(shardObjects)
                : Collections.emptyList();
    }

    @Override
    public String toString() {
        return String.format("DataHeartbeat{pointInTime='%s', shardname='%s', totalBytes=%d, usedBytes=%d, usageInPercent=%.2f, shardObjects='%s'}",
                pointInTime, shardName, totalBytes, usedBytes, usageInPercent, shardObjects);
    }

}
