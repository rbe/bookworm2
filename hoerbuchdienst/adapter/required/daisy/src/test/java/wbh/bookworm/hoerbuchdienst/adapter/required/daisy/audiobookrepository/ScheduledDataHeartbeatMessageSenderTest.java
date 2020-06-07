package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.util.concurrent.TimeUnit;

import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
class ScheduledDataHeartbeatMessageSenderTest {

    @Test
    void shouldSendHeartbeat() {
        try {
            TimeUnit.SECONDS.sleep(65
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
