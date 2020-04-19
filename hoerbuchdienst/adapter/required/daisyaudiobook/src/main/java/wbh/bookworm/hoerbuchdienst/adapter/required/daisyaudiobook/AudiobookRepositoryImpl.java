/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;

@Singleton
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
    public String[] findAll(String keyword) {
        return audiobookIndex.findAll(keyword);
    }

    @Override
    public Audiobook find(String titelnummer) {
        return audiobookMapper.audiobook(titelnummer);
    }

    @Override
    public InputStream track(final String hoerernummer, final String titelnummer, final String ident) {
        return audiobookStreamResolver.trackAsStream(titelnummer, ident);
    }

}
