package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.messaging.annotation.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RabbitListener
@Singleton
class HeartbeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatMessageReceiver.class);

    private final Map<String, HeartbeatInfo> heartbeats;

    HeartbeatMessageReceiver() {
        heartbeats = new ConcurrentHashMap<>();
    }

    @Queue(RepositoryQueues.HBD_FED_HEARTBEAT)
    public void receiveHeartbeat(@Header("x-hostname") final String hostname, final HeartbeatInfo heartbeatInfo) {
        LOGGER.info("Received {} from {}", heartbeatInfo, hostname);
        heartbeats.put(hostname, heartbeatInfo);
    }

    int numberOfHeartbeats() {
        return heartbeats.size();
    }

}
