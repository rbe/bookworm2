package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import aoc.mikrokosmos.objectstorage.api.BucketObjectRemovedEvent;

@MicronautTest
@Disabled
class ScheduledDatabeatMessageSenderTest {

    private final ApplicationEventPublisher eventPublisher;

    @Inject
    ScheduledDatabeatMessageSenderTest(final ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Test
    void shouldSendDatabeatByApplicationEvent() {
        eventPublisher.publishEvent(new BucketObjectRemovedEvent("a-bucket", "an-object"));
    }

}
