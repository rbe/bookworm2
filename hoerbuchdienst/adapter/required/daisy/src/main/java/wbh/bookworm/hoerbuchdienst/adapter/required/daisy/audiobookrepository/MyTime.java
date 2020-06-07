package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class MyTime {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private MyTime() {
        throw new AssertionError();
    }

    static ZonedDateTime now() {
        return Instant.now().atZone(ZONE_ID);
    }

    static boolean isOlderThan(long seconds, Instant instant) {
        return instant.atZone(ZONE_ID).isBefore(now().minusSeconds(seconds));
    }

}
