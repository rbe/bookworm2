package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import wbh.bookworm.hoerbuchdienst.domain.ports.ShardService;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DatabeatManager;

@Singleton
public class ShardServiceImpl implements ShardService {

    private final DatabeatManager databeatManager;

    @Inject
    public ShardServiceImpl(final DatabeatManager databeatManager) {
        this.databeatManager = databeatManager;
    }

    @Override
    public void generateDatabeat() {
        databeatManager.generate();
    }

}
