package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;
import io.micronaut.messaging.annotation.Header;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Heartbeat;

@RabbitClient(ShardingQueues.EXCH_FEDERATED_HEARTBEAT)
interface HeartbeatMessageSender {

    void send(@Header("x-shardname") String hostname, Heartbeat heartbeat);

}
