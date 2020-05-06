/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;

@Singleton
@CacheConfig("audiobookRepository")
class AudiobookRepositoryImpl implements AudiobookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImpl.class);

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final AudiobookMapper audiobookMapper;

    private final AudiobookIndex audiobookIndex;

    @Inject
    AudiobookRepositoryImpl(final AudiobookStreamResolver audiobookStreamResolver,
                            final AudiobookMapper audiobookMapper,
                            final AudiobookIndex audiobookIndex) {
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.audiobookMapper = audiobookMapper;
        this.audiobookIndex = audiobookIndex;
    }

    @Override
    public String[] findAll(final String[] keywords) {
        return audiobookIndex.findAll(keywords);
    }

    @Override
    @Cacheable
    public Audiobook find(final String titelnummer) {
        return audiobookMapper.audiobook(titelnummer);
    }

    @Override
    public InputStream trackAsStream(final String titelnummer, final String ident) {
        return audiobookStreamResolver.trackAsStream(titelnummer, ident);
    }

    @Override
    public InputStream zipAsStream(final String titelnummer) {
        return audiobookStreamResolver.zipAsStream(titelnummer);
    }

}
