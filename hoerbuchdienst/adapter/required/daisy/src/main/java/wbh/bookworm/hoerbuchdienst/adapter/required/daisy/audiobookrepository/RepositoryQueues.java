package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

public final class RepositoryQueues {

    public static final String HBD_FED_HEARTBEAT = "hbd.fed.hearbeat";

    public static final String HBD_QUEUE_HEARTBEAT = "hbd.que.hearbeat";

    public static final String HBD_RESHARD_LOCK = "hbd.reshardlock";

    public static final String HBD_RESHARD = "hbd.reshard";

    private RepositoryQueues() {
        throw new AssertionError();
    }

}
