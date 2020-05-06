/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookService;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackInfoDTO;

@Controller("/info")
public class InfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);

    private final KatalogService katalogService;

    private final AudiobookService audiobookService;

    @Inject
    public InfoController(final KatalogService katalogService,
                          final AudiobookService audiobookService) {
        this.katalogService = katalogService;
        this.audiobookService = audiobookService;
    }

    @Post(uri = "audiobook", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public AudiobookInfoAntwortDTO audiobookInfo(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        try {
            final AudiobookInfoDTO audiobookInfoDTO = katalogService.audiobookInfo(audiobookAnfrageDTO.getHoerernummer(),
                    audiobookAnfrageDTO.getTitelnummer());
            return AudiobookMapper.INSTANCE.convert(audiobookInfoDTO);
        } catch (Exception e) {
            throw new BusinessException("Hörbuch " + audiobookAnfrageDTO.getTitelnummer() + " nicht gefunden", e);
        }
    }

    @Post(uri = "playlist", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public PlaylistAntwortDTO playlist(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        try {
            final PlaylistDTO playlist = katalogService.playlist(audiobookAnfrageDTO.getHoerernummer(),
                    audiobookAnfrageDTO.getTitelnummer());
            return PlaylistMapper.INSTANCE.convert(playlist);
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public TrackInfoAntwortDTO track(@Body final TrackAnfrageDTO trackAnfrageDTO) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track-Info '{}' mit Wasserzeichen ab",
                trackAnfrageDTO.getHoerernummer(),
                trackAnfrageDTO.getTitelnummer(),
                trackAnfrageDTO.getIdent());
        try {
            final TrackInfoDTO trackInfoDTO = audiobookService.trackInfo(trackAnfrageDTO.getHoerernummer(),
                    trackAnfrageDTO.getTitelnummer(),
                    trackAnfrageDTO.getIdent());
            return TrackMapper.INSTANCE.convert(trackInfoDTO);
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

    @Mapper
    public interface AudiobookMapper {

        AudiobookMapper INSTANCE = Mappers.getMapper(AudiobookMapper.class);

        AudiobookInfoAntwortDTO convert(AudiobookInfoDTO audiobookInfoDTO);

    }

    @Mapper
    public interface PlaylistMapper {

        PlaylistMapper INSTANCE = Mappers.getMapper(PlaylistMapper.class);

        PlaylistAntwortDTO convert(PlaylistDTO playlistDTO);

    }

    @Mapper
    public interface TrackMapper {

        TrackMapper INSTANCE = Mappers.getMapper(TrackMapper.class);

        TrackInfoAntwortDTO convert(TrackInfoDTO trackInfoDTO);

    }

}
