package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class ShardHighWatermarkEvent implements Serializable {

    private final ZonedDateTime occurdOn;

    private final int highWatermark;

    public ShardHighWatermarkEvent(final int highWatermark) {
        this.highWatermark = highWatermark;
        occurdOn = ZonedDateTime.now();
    }

    public ZonedDateTime getOccurdOn() {
        return occurdOn;
    }

    public int getHighWaterMark() {
        return highWatermark;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShardHighWatermarkEvent that = (ShardHighWatermarkEvent) o;
        return highWatermark == that.highWatermark &&
                occurdOn.equals(that.occurdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(occurdOn, highWatermark);
    }

    @Override
    public String toString() {
        return String.format("ShardHighWatermarkEvent{occurdOn=%s, highWatermark=%d}", occurdOn, highWatermark);
    }

}
