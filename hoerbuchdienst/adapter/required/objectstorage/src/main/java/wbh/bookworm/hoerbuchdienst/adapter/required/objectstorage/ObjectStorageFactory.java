/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import java.net.URI;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;

import wbh.bookworm.hoerbuchdienst.domain.required.objectstorage.ObjectStorage;

@Factory
class ObjectStorageFactory {

    @EachBean(ObjectStorageConfiguration.class)
    ObjectStorage objectStorage(final ObjectStorageConfiguration configuration) {
        return new ObjectStorageImpl(URI.create(configuration.getUrl()),
                configuration.getAccessKey(), configuration.getSecureKey());
    }

}
