package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import io.micronaut.configuration.rabbitmq.annotation.Binding;
import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;
import io.micronaut.messaging.annotation.Header;

@RabbitClient
interface HeartbeatMessageSender {

    @Binding(RepositoryQueues.HBD_FED_HEARTBEAT)
    void heartbeat(@Header("x-hostname") String hostname, HeartbeatInfo heartbeatInfo);

}
