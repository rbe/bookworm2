/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl.audiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookLocationService;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardingRepository;

@Singleton
class AudiobookLocationServiceImpl implements AudiobookLocationService {

    private static final String UNKNOWN = "unknown";

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookLocationServiceImpl.class);

    private final ShardingRepository shardingRepository;

    @Inject
    AudiobookLocationServiceImpl(final ShardingRepository shardingRepository) {
        this.shardingRepository = shardingRepository;
    }

    @Override
    public String shardLocation(/* TODO Mandantenspezifisch */final String titelnummer) {
        final Optional<ShardName> maybeShardName = shardingRepository.lookupShard(titelnummer);
        if (maybeShardName.isPresent()) {
            LOGGER.debug("Looked up shard '{}' for object '{}'", maybeShardName, titelnummer);
            return maybeShardName.get().toString();
        } else {
            LOGGER.warn("Could not lookup shard for object {}", titelnummer);
        }
        return UNKNOWN;
    }

    @Override
    public boolean isLocatedLocal(/* TODO Mandantenspezifisch */final String titelnummer) {
        final Optional<ShardName> maybeShardName = shardingRepository.lookupShard(titelnummer);
        if (maybeShardName.isPresent()) {
            return new ShardName().equals(maybeShardName.get());
        } else {
            LOGGER.warn("Could not lookup shard for object {}", titelnummer);
        }
        return false;
    }

    @Override
    public boolean receive(final String titelnummer, final InputStream inputStream, final long hashValue) {
        return shardingRepository.receiveObject(titelnummer, inputStream, hashValue);
    }

}
