package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.util.List;

import io.micronaut.configuration.rabbitmq.annotation.Binding;
import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;

@RabbitClient
interface ReshardingMessageSender {

    @Binding(RepositoryQueues.HBD_RESHARD_LOCK)
    void lock(boolean lock);

    @Binding(RepositoryQueues.HBD_RESHARD)
    void send(List<ShardObject> data);

}
