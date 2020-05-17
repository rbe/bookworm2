/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookindex;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookViews;

import aoc.mikrokosmos.elasticsearch.api.IndexClient;

@Singleton
class AudiobookIndexImpl implements AudiobookIndex {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookIndexImpl.class);

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final AudiobookMapper audiobookMapper;

    private final ObjectMapper objectMapper;

    private final IndexClient indexClient;

    @Inject
    AudiobookIndexImpl(final AudiobookStreamResolver audiobookStreamResolver,
                       final AudiobookMapper audiobookMapper,
                       final ObjectMapper objectMapper,
                       final IndexClient indexClient) {
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.audiobookMapper = audiobookMapper;
        this.objectMapper = objectMapper;
        this.indexClient = indexClient;
    }

    @Override
    public boolean index() {
        final List<Path> paths = audiobookStreamResolver.listAll();
        final List<String> audiobooksAsJson = paths.stream()
                .peek(p -> LOGGER.info("{}", p.getFileName()))
                // TODO "Kapitel" Suffix ist mandantenspezifisch
                .map(p -> p.getFileName().toString().replace("Kapitel", ""))
                .map(audiobookMapper::audiobook)
                .map(audiobook -> {
                    try {
                        return objectMapper.writerWithView(AudiobookViews.SearchIndex.class)
                                .writeValueAsString(audiobook);
                    } catch (JsonProcessingException e) {
                        LOGGER.error("", e);
                        return null;
                    }
                })
                .filter(Predicate.not("null"::equalsIgnoreCase))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
        return indexClient.bulkIndex(audiobooksAsJson);
    }

    @Override
    public String[] findAll(String[] keywords) {
        return indexClient.findAll(keywords).getHits()
                .stream()
                .map(hit -> hit.get("titelnummer").toString())
                .collect(Collectors.toUnmodifiableList())
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return String.format("AudiobookIndexImpl{audiobookMapper=%s, objectMapper=%s, elasticsearchClient=%s}",
                audiobookMapper, objectMapper, indexClient);
    }

}
