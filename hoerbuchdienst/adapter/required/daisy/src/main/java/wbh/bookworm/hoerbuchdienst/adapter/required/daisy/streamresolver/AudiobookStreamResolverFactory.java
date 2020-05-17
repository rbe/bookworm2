/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.util.Locale;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.inject.qualifiers.Qualifiers;

import aoc.mikrokosmos.objectstorage.api.BucketObjectStorage;

@Factory
class AudiobookStreamResolverFactory {

    private final BeanContext beanContext;

    @Property(name = ResolverConfigurationKeys.HOERBUCHDIENST_REPOSITORY_TYPE)
    private String repositoryType;

    @Property(name = ResolverConfigurationKeys.HOERBUCHDIENST_REPOSITORY_OBJECTSTORAGE_NAME)
    private String repositoryObjectStorageName;

    @Property(name = ResolverConfigurationKeys.HOERBUCHDIENST_REPOSITORY_LOCALDISK_URI)
    private Path audiobookDirectory;

    @Inject
    AudiobookStreamResolverFactory(final BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Bean
    @Named("ByConfiguration")
    AudiobookStreamResolver audiobookStreamResolver() {
        if (null != repositoryType && !repositoryType.isBlank()) {
            switch (repositoryType.toLowerCase(Locale.ENGLISH)) {
                case "localdisk":
                    return new LocalDiskAudiobookStreamResolverImpl(audiobookDirectory);
                case "objectstorage":
                    final BucketObjectStorage bucketObjectStorage = beanContext.getBean(BucketObjectStorage.class,
                            Qualifiers.byName(repositoryObjectStorageName));
                    return new ObjectStorageAudiobookStreamResolverImpl(bucketObjectStorage);
                default:
                    throw new AudiobookStreamResolverException(
                            String.format("Unsupported repository type '%s'", repositoryType));
            }
        } else {
            throw new AudiobookStreamResolverException("Property hoerbuchdienst.repository.type not set");
        }
    }

}
