/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.micronaut.context.event.ApplicationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistEntryDTO;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;

@Singleton
public class KatalogServiceImpl implements KatalogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KatalogServiceImpl.class);

    private final AudiobookIndex audiobookIndex;

    private final AudiobookRepository audiobookRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Inject
    public KatalogServiceImpl(final AudiobookIndex audiobookIndex,
                              final AudiobookRepository audiobookRepository,
                              final ApplicationEventPublisher applicationEventPublisher) {
        this.audiobookIndex = audiobookIndex;
        this.audiobookRepository = audiobookRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public PlaylistDTO playlist(final String hoerernummer, final String titelnummer) {
        final Audiobook audiobook = audiobookRepository.find(titelnummer);
        if (null == audiobook) {
            throw new IllegalStateException("Hörbuch nicht gefunden");
        }
        LOGGER.info("Hörbuch '{}': Erstelle Hörbuch mit Playlist", titelnummer);
        final PlaylistDTO playlistDTO = new PlaylistDTO();
        final List<PlaylistEntryDTO> playlistEntries = audiobook.getAudiotracks().stream()
                .map(t -> {
                    final Double[] clips = Arrays.stream(t.getAudioclips())
                            .filter(clip -> 0L < clip.getBegin().toMillis())
                            .map(clip -> clip.getBegin().toMillis() / 1000.0d)
                            .collect(Collectors.toUnmodifiableList())
                            .toArray(Double[]::new);
                    return new PlaylistEntryDTO(t.getTitle(),
                            t.getAudioclips()[0].getFilename(),
                            clips);
                })
                .collect(Collectors.toUnmodifiableList());
        playlistDTO.addAll(playlistEntries);
        applicationEventPublisher.publishEvent(new PlaylistCreatedEvent());
        return playlistDTO;
    }

    @Override
    public AudiobookInfoDTO audiobookInfo(final String hoerernummer, final String titelnummer) {
        final Audiobook audiobook = audiobookRepository.find(titelnummer);
        if (null == audiobook) {
            throw new IllegalStateException(String.format("Hörbuch %s nicht gefunden", titelnummer));
        }
        return new AudiobookInfoDTO(titelnummer, audiobook.getTitle(),
                audiobook.getAuthor(), audiobook.getNarrator(),
                audiobook.getTimeInThisSmil());
    }

    public static class PlaylistCreatedEvent {
    }

    @Override
    public List<AudiobookInfoDTO> findAll(final String hoerernummer, final String[] keywords) {
        return Arrays.stream(audiobookRepository.findAll(keywords))
                .map(titelnummer -> audiobookInfo(hoerernummer, titelnummer))
                .collect(Collectors.toUnmodifiableList());
    }

    // TODO Automatisieren; hier nur für Testzwecke
    @Override
    public boolean index() {
        return audiobookIndex.index();
    }

}
