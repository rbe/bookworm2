package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import io.micronaut.rabbitmq.connect.ChannelPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(env = "dev")
public class ChannelPoolListener extends ChannelInitializer {

    static final String HBD_FED_HEARTBEAT = "heartbeat";

    static final String HBD_QUEUE_HEARTBEAT = "heartbeat";

    static final String HBD_FED_DATABEAT = "databeat";

    static final String HBD_QUEUE_DATABEAT = "databeat";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelPoolListener.class);

    @Override
    public ChannelPool onCreated(final BeanCreatedEvent<ChannelPool> event) {
        return super.onCreated(event);
    }

    @Override
    public void initialize(final Channel channel) throws IOException {
        final Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 100);
        LOGGER.info("Setting up exchange and queue for heartbeats");
        channel.exchangeDeclare(HBD_FED_HEARTBEAT, BuiltinExchangeType.FANOUT, true, false, args);
        channel.queueDeclare(HBD_QUEUE_HEARTBEAT, true, false, false, args);
        channel.queueBind(HBD_QUEUE_HEARTBEAT, HBD_FED_HEARTBEAT, "");
        LOGGER.info("Setting up exchange and queue for databeats");
        channel.exchangeDeclare(HBD_FED_DATABEAT, BuiltinExchangeType.FANOUT, true, false, args);
        channel.queueDeclare(HBD_QUEUE_DATABEAT, true, false, false, args);
        channel.queueBind(HBD_QUEUE_DATABEAT, HBD_FED_DATABEAT, "");
        /*
        LOGGER.info("Setting up exchange and queue for resharding");
        channel.exchangeDeclare(RepositoryQueues.HBD_RESHARD, BuiltinExchangeType.FANOUT, true, false, args);
        channel.queueDeclare(RepositoryQueues.HBD_RESHARD, true, false, false, args);
        channel.queueBind(RepositoryQueues.HBD_RESHARD, RepositoryQueues.HBD_RESHARD, "");
        LOGGER.info("Setting up queue {}", RepositoryQueues.HBD_RESHARD_LOCK);
        channel.queueDeclare(RepositoryQueues.HBD_RESHARD_LOCK, true, false, false, args);
        */
    }

}
