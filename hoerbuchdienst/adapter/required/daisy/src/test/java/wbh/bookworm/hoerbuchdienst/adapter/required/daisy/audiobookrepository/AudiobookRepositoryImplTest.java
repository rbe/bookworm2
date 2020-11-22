package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;

@MicronautTest
@Disabled
class AudiobookRepositoryImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImplTest.class);

    @Inject
    private AudiobookRepository audiobookRepository;

    /*
    @Inject
    private HeartbeatMessageReceiver heartbeatMessageReceiver;
    @MockBean(HeartbeatMessageReceiver.class)
    HeartbeatMessageReceiver heartbeatMessageReceiver() {
        final HeartbeatMessageReceiver mock = Mockito.mock(HeartbeatMessageReceiver.class);
        Mockito.doNothing()
                .when(mock)
                .receiveHeartbeat(Mockito.any(), Mockito.any());
        Mockito.when(mock.numberOfHeartbeats())
                .thenReturn(2);
        return mock;
    }

    @InjectMocks
    private ShardDistributionStrategy shardDistributionStrategy;
    @MockBean(KeyBasedShardDistributionStrategyImpl.class)
    ShardDistributionStrategy shardDistributionStrategy() {
        final KeyBasedShardDistributionStrategyImpl mock = Mockito.mock(KeyBasedShardDistributionStrategyImpl.class);
        Mockito.doAnswer(invocation -> {
            final Object[] arguments = invocation.getArguments();
            arguments[0] = shardObjects;
            return null;
        })
                .when(mock)
                .calculate(Mockito.anyList());
        return mock;
    }

    @Inject @InjectMocks
    private ReshardingMessageReceiver reshardingMessageReceiver;

    @MockBean(AudiobookRepositoryImpl.class)
    AudiobookRepository audiobookRepository() {
        final AudiobookRepository mock = Mockito.mock(AudiobookRepository.class);
        return mock;
    }

    @MockBean(ReshardingMessageReceiver.class)
    ReshardingMessageReceiver reshardingMessageReceiver() {
        final ReshardingMessageReceiver mock = Mockito.mock(ReshardingMessageReceiver.class);
        Mockito.doAnswer(invocation -> {
            final Object[] arguments = invocation.getArguments();
            arguments[0] = shardObjects;
            return null;
        })
                .when(mock)
                .receiveData(Mockito.anyList());
        return mock;
    }
    */

    @Test
    void shouldReshard() {
        // wait for heartbeat
        wait10Secs();
        //audiobookRepository.startResharding();
        wait10Secs();
    }

    private void wait10Secs() {
        try {
            TimeUnit.SECONDS.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    @Test
    void allEntriesByKey() {
        LOGGER.info("{}", audiobookRepository.allEntriesByKey());
    }

}
