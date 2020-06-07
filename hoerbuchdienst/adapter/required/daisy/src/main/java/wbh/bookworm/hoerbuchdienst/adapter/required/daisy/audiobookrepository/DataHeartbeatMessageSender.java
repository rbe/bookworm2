package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;
import io.micronaut.messaging.annotation.Header;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeat;

@RabbitClient(RepositoryQueues.EXCH_FEDERATED_DATAHEARTBEAT)
interface DataHeartbeatMessageSender {

    void send(@Header("x-hostname") String hostname, DataHeartbeat dataHeartbeat);

}
