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
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import aoc.mikrokosmos.crypto.messagedigest.MessageDigester;

@Introspected
public final class Databeat implements Serializable {

    private static final long serialVersionUID = 2963762098263689022L;

    private final Instant pointInTime;

    private final ShardName shardName;

    private final long totalBytes;

    private final long usedBytes;

    private final double usageInPercent;

    private final List<ShardAudiobook> shardAudiobooks;

    private final String consentHash;

    @JsonCreator
    public Databeat(@JsonProperty("pointInTime") final Instant pointInTime,
                    @JsonProperty("shardName") final ShardName shardName,
                    @JsonProperty("totalBytes") final long totalBytes,
                    @JsonProperty("usedBytes") final long usedBytes,
                    @JsonProperty("shardAudiobooks") final List<ShardAudiobook> shardAudiobooks,
                    @JsonProperty("consentHash") final String consentHash) {
        Objects.requireNonNull(pointInTime, "PointInTime missing");
        this.pointInTime = pointInTime;
        Objects.requireNonNull(shardName, "ShardName missing");
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
        if (null == shardAudiobooks) {
            this.shardAudiobooks = Collections.emptyList();
        } else {
            this.shardAudiobooks = Collections.unmodifiableList(shardAudiobooks);
        }
        if (null == consentHash) {
            this.consentHash = "";
        } else {
            this.consentHash = consentHash;
        }
    }

    public Instant getPointInTime() {
        return pointInTime;
    }

    @JsonIgnore
    public ZonedDateTime pointInTimeAsZonedDateTime() {
        return pointInTime.atZone(ZoneId.systemDefault());
    }

    public ShardName getShardName() {
        return shardName;
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

    public int numberOfObjects() {
        return shardAudiobooks.size();
    }

    public String getConsentHash() {
        return consentHash;
    }

    /**
     * Hash over all hashes of all audiobooks in this shard.
     */
    public String hashValue() {
        return MessageDigester.sha256OfUTF8(shardAudiobooks.stream()
                .map(ShardAudiobook::getHashValue)
                .collect(Collectors.joining()));
    }

    @Override
    public String toString() {
        return String.format("Databeat{getPointInTime='%s', shardName='%s', totalBytes=%d, usedBytes=%d, usageInPercent=%.2f, shardAudiobooks='%s', consentHash='%s'}",
                pointInTime, shardName, totalBytes, usedBytes, usageInPercent, shardAudiobooks, consentHash);
    }

}
