package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

public final class RepositoryQueues {

    public static final String EXCH_FEDERATED_HEARTBEAT = "federated.heartbeat";

    public static final String QUEUE_HEARTBEAT = "heartbeat";

    public static final String EXCH_FEDERATED_DATAHEARTBEAT = "federated.databeat";

    public static final String QUEUE_DATAHEARTBEAT = "databeat";

    private RepositoryQueues() {
        throw new AssertionError();
    }

}
