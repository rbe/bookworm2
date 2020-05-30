package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.micronaut.configuration.rabbitmq.connect.ChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
final class ChannelPoolListener extends ChannelInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelPoolListener.class);

    @Override
    public void initialize(Channel channel) throws IOException {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 100);
        LOGGER.info("Setting up fanout {}", RepositoryQueues.HBD_FED_HEARTBEAT);
        channel.exchangeDeclare(RepositoryQueues.HBD_FED_HEARTBEAT, BuiltinExchangeType.FANOUT, false, true, args);
        channel.queueDeclare(RepositoryQueues.HBD_QUEUE_HEARTBEAT, false, false, false, args);
        channel.queueBind(RepositoryQueues.HBD_QUEUE_HEARTBEAT, RepositoryQueues.HBD_FED_HEARTBEAT, "");
        LOGGER.info("Setting up queue {}", RepositoryQueues.HBD_RESHARD);
        channel.exchangeDeclare(RepositoryQueues.HBD_RESHARD, BuiltinExchangeType.FANOUT, false, true, args);
        channel.queueDeclare(RepositoryQueues.HBD_RESHARD, false, false, false, args);
        channel.queueBind(RepositoryQueues.HBD_RESHARD, RepositoryQueues.HBD_RESHARD, "");
        LOGGER.info("Setting up queue {}", RepositoryQueues.HBD_RESHARD_LOCK);
        channel.queueDeclare(RepositoryQueues.HBD_RESHARD_LOCK, false, false, false, args);
    }

}
