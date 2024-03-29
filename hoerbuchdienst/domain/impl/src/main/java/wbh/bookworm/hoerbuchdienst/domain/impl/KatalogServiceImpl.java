/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventPublisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistCreatedEvent;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistEntryDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.WatermarkedTrackInfo;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;

import aoc.mikrokosmos.io.fs.FilesUtils;

@Singleton
class KatalogServiceImpl implements KatalogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KatalogServiceImpl.class);

    private final AudiobookIndex audiobookIndex;

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value(/* TODO Mandantenspezifisch */"${hoerbuchdienst.piracy.inquiry.urlprefix}")
    private String piracyInquiryUrlPrefix;

    @Inject
    KatalogServiceImpl(final AudiobookIndex audiobookIndex,
                       final AudiobookRepository audiobookRepository,
                       final Watermarker watermarker,
                       final ApplicationEventPublisher applicationEventPublisher) {
        this.audiobookIndex = audiobookIndex;
        this.audiobookRepository = audiobookRepository;
        this.watermarker = watermarker;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public AudiobookInfoDTO audiobookInfo(final String titelnummer) {
        final Audiobook audiobook = audiobookRepository.find(titelnummer);
        if (null == audiobook) {
            throw new IllegalStateException(String.format("Hörbuch %s nicht gefunden", titelnummer));
        }
        return new AudiobookInfoDTO(titelnummer, audiobook.getTitle(),
                audiobook.getAuthor(), audiobook.getNarrator(), audiobook.getTimeInThisSmil());
    }

    @Override
    public TrackInfoDTO trackInfo(final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = audiobookRepository.trackAsFile(hoerernummer, titelnummer, ident);
        final String watermark = watermarker.makeWatermark("06", hoerernummer, titelnummer);
        final WatermarkedTrackInfo watermarkedTrackInfo = watermarker.trackInfo(watermark, piracyInquiryUrlPrefix, tempMp3File);
        FilesUtils.tryDelete(tempMp3File);
        return TrackDtoMapper.INSTANCE.convert(watermarkedTrackInfo, titelnummer, ident);
    }

    @Override
    public PlaylistDTO playlist(final String titelnummer) {
        final Audiobook audiobook = audiobookRepository.find(titelnummer);
        if (null == audiobook) {
            throw new IllegalStateException("Hörbuch '" + titelnummer + "' nicht gefunden");
        }
        final List<PlaylistEntryDTO> playlistEntries = audiobook.getAudiotracks()
                .stream()
                .map(t -> {
                    final Double[] clips = Arrays.stream(t.getAudioclips())
                            .filter(clip -> 0L < clip.getBegin().toMillis())
                            .map(clip -> clip.getBegin().toMillis() / 1_000.0d)
                            .collect(Collectors.toUnmodifiableList())
                            .toArray(Double[]::new);
                    final PlaylistEntryDTO playlistEntryDTO = new PlaylistEntryDTO(t.getTitle(),
                            t.getAudioclips()[0].getFilename(),
                            clips);
                    LOGGER.debug("{}", playlistEntryDTO);
                    return playlistEntryDTO;
                })
                .collect(Collectors.toUnmodifiableList());
        final PlaylistDTO playlistDTO = new PlaylistDTO(titelnummer, playlistEntries);
        LOGGER.debug("Hörbuch '{}': Playlist erstellt: {}", titelnummer, playlistDTO);
        applicationEventPublisher.publishEvent(new PlaylistCreatedEvent());
        return playlistDTO;
    }

    @Override
    public List<Path> playlistFuerHoerprobe(final String titelnummer) {
        return audiobookRepository.findMp3s(titelnummer);
    }

    // TODO Automatisieren; hier nur für Testzwecke
    @Override
    public boolean index() {
        return audiobookIndex.index();
    }

    @Override
    public List<AudiobookInfoDTO> findAll(final String[] keywords) {
        return Arrays.stream(audiobookIndex.findAll(keywords))
                .map(this::audiobookInfo)
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
