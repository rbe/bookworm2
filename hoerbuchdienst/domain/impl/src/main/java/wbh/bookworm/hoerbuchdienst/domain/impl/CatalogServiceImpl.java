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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.CatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistEntry;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookRepository;

@Singleton
public final class CatalogServiceImpl implements CatalogService {

    private final AudiobookRepository audiobookRepository;

    @Inject
    public CatalogServiceImpl(final AudiobookRepository audiobookRepository) {
        this.audiobookRepository = audiobookRepository;
    }

    @Override
    public AudiobookInfoDTO audiobookInfo(final String titelnummer) {
        final Audiobook audiobook = audiobookRepository.find(titelnummer);
        if (null == audiobook) {
            throw new IllegalStateException(String.format("Hörbuch %s nicht gefunden", titelnummer));
        }
        return new AudiobookInfoDTO(titelnummer, audiobook.getTitle(),
                audiobook.getAuthor(), audiobook.getNarrator(),
                audiobook.getTimeInThisSmil());
    }

    @Override
    public PlaylistDTO playlist(final String titelnummer) {
        final Audiobook audiobook = audiobookRepository.find(titelnummer);
        if (null == audiobook) {
            throw new IllegalStateException("Hörbuch nicht gefunden");
        }
        LOGGER.info("Hörbuch '{}': Erstelle Hörbuch mit Playlist", titelnummer);
        final PlaylistDTO playlistDTO = new PlaylistDTO();
        final List<PlaylistEntry> playlistEntries = Arrays.stream(audiobook.getAudiotracks())
                .map(t -> {
                    final Double[] clips = Arrays.stream(t.getAudioclips())
                            .filter(c -> c.getBegin().toMillis() > 0.0d)
                            .map(c -> c.getBegin().toMillis() / 1000.0d)
                            .collect(Collectors.toUnmodifiableList())
                            .toArray(Double[]::new);
                    return new PlaylistEntry(t.getTitle(),
                            t.getAudioclips()[0].getFilename(),
                            clips);
                })
                .collect(Collectors.toUnmodifiableList());
        playlistDTO.addAll(playlistEntries);
        return playlistDTO;
    }

    @Override
    public List<AudiobookInfoDTO> findAll(final String keyword) {
        return Arrays.stream(audiobookRepository.findAll(keyword))
                .map(this::audiobookInfo)
                .collect(Collectors.toUnmodifiableList());
    }

    // TODO Automatisieren; hier nur für Testzwecke
    @Override
    public boolean index() {
        return audiobookRepository.index();
    }

}
