package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import io.micronaut.messaging.annotation.Header;
import io.micronaut.rabbitmq.annotation.RabbitClient;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;

@RabbitClient(ShardingQueues.EXCH_FEDERATED_DATABEAT)
interface DatabeatMessageSender {

    void send(@Header("x-shardname") String shardname, Databeat databeat);

}
