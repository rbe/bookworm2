/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl.audiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.micronaut.context.event.ApplicationEventPublisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.PlaylistEntryDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.TrackInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.WatermarkedTrackInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;

@Singleton
public class KatalogServiceImpl implements KatalogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KatalogServiceImpl.class);

    // TODO Konfiguration pro Mandant
    private static final String PIRACY_INQUIRY_URL_PREFIX = "https://wbh-online.de/ausleihe-anfragen";

    private final AudiobookIndex audiobookIndex;

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Inject
    public KatalogServiceImpl(final AudiobookIndex audiobookIndex,
                              final AudiobookRepository audiobookRepository,
                              final Watermarker watermarker,
                              final ApplicationEventPublisher applicationEventPublisher) {
        this.audiobookIndex = audiobookIndex;
        this.audiobookRepository = audiobookRepository;
        this.watermarker = watermarker;
        this.applicationEventPublisher = applicationEventPublisher;
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

    @Override
    public TrackInfoDTO trackInfo(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = audiobookRepository.localCopyOfTrack(hoerernummer, titelnummer, ident,
                "trackinfo");
        final String watermark = watermarker.makeWatermark(hoerernummer, titelnummer);
        final WatermarkedTrackInfo watermarkedTrackInfo = watermarker.trackInfo(watermark,
                PIRACY_INQUIRY_URL_PREFIX, tempMp3File);
        FilesHelper.tryDelete(tempMp3File);
        return TrackDtoMapper.INSTANCE.convert(watermarkedTrackInfo, titelnummer, ident);
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

    // TODO Automatisieren; hier nur für Testzwecke
    @Override
    public boolean index() {
        return audiobookIndex.index();
    }

    @Override
    public List<AudiobookInfoDTO> findAll(final String hoerernummer, final String[] keywords) {
        return Arrays.stream(audiobookIndex.findAll(keywords))
                .map(titelnummer -> audiobookInfo(hoerernummer, titelnummer))
                .collect(Collectors.toUnmodifiableList());
    }

    @Mapper
    public interface TrackDtoMapper {

        TrackDtoMapper INSTANCE = Mappers.getMapper(TrackDtoMapper.class);

        @Mapping(source = "titelnummer", target = "titelnummer")
        @Mapping(source = "ident", target = "ident")
        TrackInfoDTO convert(WatermarkedTrackInfo watermarkedTrackInfo, String titelnummer, String ident);

    }

}
