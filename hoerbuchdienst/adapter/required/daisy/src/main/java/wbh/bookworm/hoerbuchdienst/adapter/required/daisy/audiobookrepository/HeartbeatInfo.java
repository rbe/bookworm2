package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class HeartbeatInfo implements Serializable {

    private static final long serialVersionUID = 2963762098263689022L;

    private final String hostname;

    private final long totalBytes;

    private final long usedBytes;

    private final double usageInPercent;

    @JsonCreator
    public HeartbeatInfo(@JsonProperty("hostname") final String hostname,
                         @JsonProperty("totalBytes") final long totalBytes,
                         @JsonProperty("usedBytes") final long usedBytes) {
        this.hostname = hostname;
        this.totalBytes = totalBytes;
        this.usedBytes = usedBytes;
        usageInPercent = BigDecimal.valueOf(((double) usedBytes / (double) totalBytes) * 100.0d)
                .setScale(2, RoundingMode.CEILING)
                .doubleValue();
    }

    public String getHostname() {
        return hostname;
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

    @Override
    public String toString() {
        return String.format("HeartbeatInfo{hostname='%s', totalBytes=%d, usedBytes=%d, usageInPercent=%s}",
                hostname, totalBytes, usedBytes, usageInPercent);
    }

}
