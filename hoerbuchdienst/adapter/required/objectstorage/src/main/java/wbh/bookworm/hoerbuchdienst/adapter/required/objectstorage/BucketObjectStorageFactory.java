/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import javax.inject.Inject;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.inject.qualifiers.Qualifiers;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.BucketObjectStorage;
import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

@Factory
class BucketObjectStorageFactory {

    @Inject
    private BeanContext beanContext;

    @EachBean(ObjectStorageConfiguration.class)
    BucketObjectStorage bucketObjectStorage(final ObjectStorageConfiguration configuration) {
        final ObjectStorage objectStorage = beanContext.getBean(ObjectStorage.class,
                Qualifiers.byName(configuration.getName()));
        return new BucketObjectStorageImpl(objectStorage, configuration.getBucketName());
    }

}
