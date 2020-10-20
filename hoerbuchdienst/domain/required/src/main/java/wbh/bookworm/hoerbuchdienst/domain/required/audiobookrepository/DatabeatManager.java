package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DatabeatManager {

    void generate();

    Optional<Databeat> getMyDatabeat();

    void remember(ShardName shardName, Databeat databeat);

    void forget(ShardName shardName);

    boolean canRedistribute();

    Optional<Long> totalSizeOfAllObjects();

    Set<ShardName> allShardNames();

    int getHeartbeatHighWatermark();

    int numberOfDatabeats();

    long numerOfObjects();

    List<ShardAudiobook> allShardsAudiobooks();

    Optional<ShardName> findShardNameForAudiobook(String titelnummer);

    boolean isConsent();

}
