/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aoc.mikrokosmos.objectstorage.api.BucketEvent;
import aoc.mikrokosmos.objectstorage.api.BucketObjectRemovedEvent;
import aoc.mikrokosmos.objectstorage.api.BucketObjectStorage;

@Singleton
public class BucketObjectRemovedEventApplicationEventAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BucketObjectRemovedEventApplicationEventAdapter.class);

    private final BucketObjectStorage bucketObjectStorage;

    private final ApplicationEventPublisher eventPublisher;

    @Inject
    public BucketObjectRemovedEventApplicationEventAdapter(final BucketObjectStorage bucketObjectStorage,
                                                           final ApplicationEventPublisher eventPublisher) {
        this.bucketObjectStorage = bucketObjectStorage;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onStartup(final StartupEvent startupEvent) {
        LOGGER.info("Registering bucket notification listener", startupEvent);
        bucketObjectStorage.registerNotificationListener(objectStorageEvent -> {
                    final BucketObjectRemovedEvent event = new BucketObjectRemovedEvent(
                            objectStorageEvent.getBucketName(),
                            objectStorageEvent.getObjectName());
                    eventPublisher.publishEvent(event);
                },
                BucketEvent.S3_OBJECT_REMOVED);
    }

}
