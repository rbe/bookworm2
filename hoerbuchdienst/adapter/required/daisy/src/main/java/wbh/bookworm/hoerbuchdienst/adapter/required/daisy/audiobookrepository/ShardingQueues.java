package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

final class ShardingQueues {

    static final String EXCH_FEDERATED_HEARTBEAT = "federated.heartbeat";

    static final String QUEUE_HEARTBEAT = "heartbeat";

    static final String EXCH_FEDERATED_DATABEAT = "federated.databeat";

    static final String QUEUE_DATABEAT = "databeat";

    private ShardingQueues() {
        throw new AssertionError();
    }

}
