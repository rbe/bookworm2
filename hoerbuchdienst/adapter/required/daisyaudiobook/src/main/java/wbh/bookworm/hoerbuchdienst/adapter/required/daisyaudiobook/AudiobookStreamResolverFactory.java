/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Locale;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.Property;
import io.micronaut.inject.qualifiers.Qualifiers;

import aoc.mikrokosmos.objectstorage.api.BucketObjectStorage;

@Factory
class AudiobookStreamResolverFactory {

    private final BeanContext beanContext;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_REPOSITORY_TYPE)
    private String repositoryType;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_REPOSITORY_OBJECTSTORAGE_NAME)
    private String repositoryObjectStorageName;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_REPOSITORY_LOCALDISK_URI)
    private Path localdiskUri;

    @Inject
    AudiobookStreamResolverFactory(final BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Bean
    AudiobookStreamResolver audiobookStreamResolver() {
        if (null != repositoryType && !repositoryType.isBlank()) {
            switch (repositoryType.toLowerCase(Locale.ENGLISH)) {
                case "localdisk":
                    return localDiskAudiobookStreamResolver();
                case "objectstorage":
                    return objectStorageAudiobookStreamResolver(repositoryObjectStorageName);
                default:
                    throw new AudiobookMapperException(String.format("Unsupported repository type '%s'", repositoryType));
            }
        } else {
            throw new AudiobookMapperException("Property hoerbuchdienst.repository.type not set");
        }
    }

    @Bean
    LocalDiskAudiobookStreamResolverImpl localDiskAudiobookStreamResolver() {
        return new LocalDiskAudiobookStreamResolverImpl(localdiskUri);
    }

    @Bean
    ObjectStorageAudiobookStreamResolverImpl objectStorageAudiobookStreamResolver(@Parameter String qualifier) {
        final BucketObjectStorage bucketObjectStorage = beanContext.getBean(BucketObjectStorage.class,
                Qualifiers.byName(qualifier));
        return new ObjectStorageAudiobookStreamResolverImpl(bucketObjectStorage);
    }

}
