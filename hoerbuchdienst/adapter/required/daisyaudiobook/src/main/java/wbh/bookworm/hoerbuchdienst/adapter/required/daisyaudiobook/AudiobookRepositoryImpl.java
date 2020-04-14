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
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookViews;

@Singleton
class AudiobookRepositoryImpl implements AudiobookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImpl.class);

    private static final byte[] EMPTY_BYTES = new byte[0];

    private final AudiobookMapper audiobookMapper;

    private final ObjectMapper objectMapper;

    private final ElasticsearchClient elasticsearchClient;

    @Property(name = "hoerbuchdienst.repository.uri")
    private Path streamBase;

    @Inject
    public AudiobookRepositoryImpl(final AudiobookMapper audiobookMapper,
                                   final ObjectMapper objectMapper,
                                   final ElasticsearchClient elasticsearchClient) {
        this.audiobookMapper = audiobookMapper;
        this.objectMapper = objectMapper;
        this.elasticsearchClient = elasticsearchClient;
    }

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
    public Audiobook find(String titelnummer) {
        return audiobookMapper.from(titelnummer);
    }

    @Override
    public byte[] read(final String titelnummer, final String ident) {
        byte[] result;
        final Path mp3 = streamBase.resolve(titelnummer + "Kapitel").resolve(ident);
        try {
            Mp3File mp3file = new Mp3File(mp3);
            System.out.println(mp3file);
            //mp3file.getId3v1Tag().setComment("".substring(0,Math.min(30,));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        try {
            result = Files.readAllBytes(mp3);
        } catch (IOException e) {
            log.error("Cannot stream track", e);
            result = EMPTY_BYTES;
        }
        return result;
    }

}
