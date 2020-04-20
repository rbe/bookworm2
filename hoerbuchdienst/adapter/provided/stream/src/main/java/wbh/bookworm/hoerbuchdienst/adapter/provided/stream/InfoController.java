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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.Mp3RepositoryService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackDTO;

@Controller("/info")
public class InfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);

    private final KatalogService katalogService;

    private final Mp3RepositoryService mp3RepositoryService;

    @Inject
    public InfoController(final KatalogService katalogService,
                          final Mp3RepositoryService mp3RepositoryService) {
        this.katalogService = katalogService;
        this.mp3RepositoryService = mp3RepositoryService;
    }

    @Post(uri = "audiobook", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public AudiobookInfoDTO audiobook(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        try {
            return katalogService.audiobookInfo(audiobookAnfrageDTO.getHoerernummer(),
                    audiobookAnfrageDTO.getTitelnummer());
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

    @Post(uri = "playlist", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public PlaylistDTO playlist(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        try {
            return katalogService.playlist(audiobookAnfrageDTO.getHoerernummer(),
                    audiobookAnfrageDTO.getTitelnummer());
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public TrackDTO track(@Body final TrackAnfrageDTO trackAnfrageDTO) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track-Info '{}' mit Wasserzeichen ab",
                trackAnfrageDTO.getHoerernummer(),
                trackAnfrageDTO.getTitelnummer(),
                trackAnfrageDTO.getIdent());
        try {
            return mp3RepositoryService.trackInfo(trackAnfrageDTO.getHoerernummer(),
                    trackAnfrageDTO.getTitelnummer(),
                    trackAnfrageDTO.getIdent());
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

}
