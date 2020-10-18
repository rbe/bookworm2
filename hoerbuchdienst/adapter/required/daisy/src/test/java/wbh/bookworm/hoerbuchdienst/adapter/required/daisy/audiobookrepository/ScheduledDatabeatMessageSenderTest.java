package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import aoc.mikrokosmos.objectstorage.api.BucketObjectRemovedEvent;

@MicronautTest
class ScheduledDatabeatMessageSenderTest {

    private ApplicationEventPublisher eventPublisher;

    @Inject
    ScheduledDatabeatMessageSenderTest(final ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Test
    void shouldSendDatabeat() {
        eventPublisher.publishEvent(new BucketObjectRemovedEvent("a-bucket", "an-object"));
    }

}
