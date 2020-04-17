/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.TaskScheduler;
import io.micronaut.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

@Singleton
public class BucketNotificationApplicationEventAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BucketNotificationApplicationEventAdapter.class);

    private final ObjectStorage objectStorage;

    private final ApplicationEventPublisher eventPublisher;

    private final TaskScheduler taskScheduler;

    @Inject
    public BucketNotificationApplicationEventAdapter(@Named("minio") final ObjectStorage objectStorage,
                                                     final TaskScheduler taskScheduler,
                                                     final ApplicationEventPublisher eventPublisher) {
        this.objectStorage = objectStorage;
        this.taskScheduler = taskScheduler;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    @Async
    public void onStartup(final StartupEvent startupEvent) {
        LOGGER.info("{}", startupEvent);
    }

}
