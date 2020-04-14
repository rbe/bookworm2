/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookViews;

@Singleton
class AudiobookIndexImpl implements AudiobookIndex {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookIndexImpl.class);

    private final AudiobookMapper audiobookMapper;

    private final ObjectMapper objectMapper;

    private final ElasticsearchClient elasticsearchClient;

    @Property(name = "hoerbuchdienst.repository.uri")
    private Path streamBase;

    @Inject
    public AudiobookIndexImpl(final AudiobookMapper audiobookMapper,
                              final ObjectMapper objectMapper,
                              final ElasticsearchClient elasticsearchClient) {
        this.audiobookMapper = audiobookMapper;
        this.objectMapper = objectMapper;
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public String[] findAll(String keyword) {
        return Arrays.stream(elasticsearchClient.findAll(keyword))
                .map(hit -> hit.getSourceAsMap().get("titelnummer").toString())
                .collect(Collectors.toUnmodifiableList())
                .toArray(String[]::new);
    }

    @Override
    public boolean index() {
        List<String> audiobooksAsJson = null;
        try (final Stream<Path> paths = Files.walk(streamBase.resolve("."), 1)
                .filter(p -> p.endsWith("Kapitel"))) {
            audiobooksAsJson = paths.peek(p -> log.info("{}", p.getFileName()))
                    .map(p -> p.getFileName().toString().replace("Kapitel", ""))
                    .map(audiobookMapper::from)
                    .map(audiobook -> {
                        try {
                            return objectMapper.writerWithView(AudiobookViews.SearchIndex.class)
                                    .writeValueAsString(audiobook);
                        } catch (JsonProcessingException e) {
                            log.error("", e);
                            return null;
                        }
                    })
                    .filter(Predicate.not("null"::equalsIgnoreCase))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new ElasticsearchClientException(e);
        }
        return elasticsearchClient.bulkIndex(audiobooksAsJson);
    }

    @Override
    public String toString() {
        return String.format("AudiobookIndexImpl{audiobookMapper=%s, objectMapper=%s, elasticsearchClient=%s, streamBase=%s}",
                audiobookMapper, objectMapper, elasticsearchClient, streamBase);
    }

}
