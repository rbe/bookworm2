/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.scheduling.TaskExecutors;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

@Factory
class ObjectStorageFactory {

    private final ExecutorService executorService;

    @Inject
    ObjectStorageFactory(@Named(TaskExecutors.IO) final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @EachBean(ObjectStorageConfiguration.class)
    ObjectStorage objectStorage(final ObjectStorageConfiguration configuration) {
        return new ObjectStorageImpl(URI.create(configuration.getUrl()),
                configuration.getAccessKey(), configuration.getSecureKey(), executorService);
    }

}
