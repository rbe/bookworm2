package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;
import io.micronaut.messaging.annotation.Header;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;

@RabbitClient(RepositoryQueues.EXCH_FEDERATED_DATAHEARTBEAT)
interface DatabeatMessageSender {

    void send(@Header("x-shardname") String shardname, Databeat databeat);

}
