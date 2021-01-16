package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SmilTimeHelperTest {

    @Test
    void shouldParseHoursMinutesSeconds() {
        int secs = 10 + 5 * 60 + 60 * 60 * 2;
        //
        Duration duration = SmilTimeHelper.parse("02:05:10").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
        //
        duration = SmilTimeHelper.parse("2:05:10").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
        //
        duration = SmilTimeHelper.parse("2:5:10").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
        //
        duration = SmilTimeHelper.parse("2:5:1").orElseThrow();
        assertNotEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
    }

    @Test
    void shouldParseMinutesSeconds() {
        int secs = 10 + 5 * 60;
        //
        Duration duration = SmilTimeHelper.parse("05:10").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
        //
        duration = SmilTimeHelper.parse("5:10").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
        //
        duration = SmilTimeHelper.parse("5:1").orElseThrow();
        assertNotEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
    }

    @Test
    void shouldParseSeconds() {
        int secs = 10;
        //
        Duration duration = SmilTimeHelper.parse("0:10").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
        //
        duration = SmilTimeHelper.parse("0:1").orElseThrow();
        assertNotEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
    }

}
