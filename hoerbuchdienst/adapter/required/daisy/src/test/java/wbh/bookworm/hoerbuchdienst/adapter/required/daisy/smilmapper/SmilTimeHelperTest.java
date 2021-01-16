package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SmilTimeHelperTest {

    @Test
    void shouldParseHoursMinutesSeconds() {
        long secs = 10 + 5 * 60 + 60 * 60 * 2L;
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
    void shouldParseHoursMinutesSeconds2() {
        long secs = 16*60+55L;
        //
        Duration duration = SmilTimeHelper.parse("00:16:55").orElseThrow();
        System.out.println(duration);
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
    }

    @Test
    void shouldParseMinutesSeconds() {
        long secs = 10 + 5 * 60L;
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
        long secs = 10L;
        //
        Duration duration = SmilTimeHelper.parse("0:10").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
        //
        duration = SmilTimeHelper.parse("0:1").orElseThrow();
        assertNotEquals(Duration.of(secs, ChronoUnit.SECONDS), duration);
    }

    @Test
    void shouldParseSecondsFraction() {
        long secs = 1500;
        //
        Duration duration = SmilTimeHelper.parse("0:1.5").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.MILLIS), duration);
        //
        duration = SmilTimeHelper.parse("1.500").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.MILLIS), duration);
    }

    @Test
    void shouldParseSecondsFractionAndS() {
        long secs = 1500;
        //
        Duration duration = SmilTimeHelper.parse("0:1.5s").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.MILLIS), duration);
        //
        duration = SmilTimeHelper.parse("1.500s").orElseThrow();
        assertEquals(Duration.of(secs, ChronoUnit.MILLIS), duration);
        //
        duration = SmilTimeHelper.parse("0.000s").orElseThrow();
        assertEquals(Duration.of(0, ChronoUnit.MILLIS), duration);
    }

}
