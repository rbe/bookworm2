package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    private final List<ShardAudiobook> shardAudiobooks;

    @JsonCreator
    public DataHeartbeat(@JsonProperty("pointInTime") final Instant pointInTime,
                         @JsonProperty("shardname") final ShardName shardName,
                         @JsonProperty("totalBytes") final long totalBytes,
                         @JsonProperty("usedBytes") final long usedBytes,
                         @JsonProperty("shardAudiobooks") final List<ShardAudiobook> shardAudiobooks) {
        Objects.requireNonNull(pointInTime);
        this.pointInTime = pointInTime;
        Objects.requireNonNull(shardName);
        this.shardName = shardName;
        if (0L > totalBytes) {
            throw new IllegalArgumentException("totalBytes must be >0");
        }
        this.totalBytes = totalBytes;
        if (0L > usedBytes) {
            throw new IllegalArgumentException("usedBytes must be >=0");
        }
        this.usedBytes = usedBytes;
        usageInPercent = BigDecimal.valueOf(((double) usedBytes / (double) totalBytes) * 100.0d)
                .setScale(2, RoundingMode.CEILING)
                .doubleValue();
        Objects.requireNonNull(shardAudiobooks);
        this.shardAudiobooks = Collections.unmodifiableList(shardAudiobooks);
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

    public List<ShardAudiobook> getShardAudiobooks() {
        return null != shardAudiobooks
                ? Collections.unmodifiableList(shardAudiobooks)
                : Collections.emptyList();
    }

    @Override
    public String toString() {
        return String.format("DataHeartbeat{pointInTime='%s', shardname='%s', totalBytes=%d, usedBytes=%d, usageInPercent=%.2f, shardAudiobooks='%s'}",
                pointInTime, shardName, totalBytes, usedBytes, usageInPercent, shardAudiobooks);
    }

}
