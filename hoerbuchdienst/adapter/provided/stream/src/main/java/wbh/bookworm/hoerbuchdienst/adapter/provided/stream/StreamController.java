/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.Mp3RepositoryService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackDTO;

@OpenAPIDefinition(
        info = @Info(title = "wbh.sds", version = "0.0")
)
@Controller("/stream")
public class StreamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamController.class);

    private final KatalogService katalogService;

    private final Mp3RepositoryService mp3RepositoryService;

    @Inject
    public StreamController(final KatalogService katalogService,
                            final Mp3RepositoryService mp3RepositoryService) {
        this.katalogService = katalogService;
        this.mp3RepositoryService = mp3RepositoryService;
    }

    @ApiResponse
    @Get(uri = "/{hoerernummer}/{titelnummer}/info", produces = MediaType.APPLICATION_JSON)
    public AudiobookInfoDTO info(@PathVariable final String hoerernummer,
                                 @PathVariable final String titelnummer) {
        try {
            return katalogService.audiobookInfo(titelnummer);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @ApiResponse
    @Get(uri = "/{hoerernummer}/{titelnummer}/playlist", produces = MediaType.APPLICATION_JSON)
    public PlaylistDTO playlist(@PathVariable final String hoerernummer,
                                @PathVariable final String titelnummer) {
        try {
            return katalogService.playlist(titelnummer);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @ApiResponse
    @Get(uri = "/{hoerernummer}/{titelnummer}/track/{ident}/info", produces = MediaType.APPLICATION_JSON)
    public TrackDTO trackInfo(@PathVariable final String hoerernummer,
                              @PathVariable final String titelnummer,
                              @PathVariable final String ident) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track-Info '{}' mit Wasserzeichen ab",
                hoerernummer, titelnummer, ident);
        try {
            return mp3RepositoryService.trackInfo(hoerernummer, titelnummer, ident);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @ApiResponse
    @Get(uri = "/{hoerernummer}/{titelnummer}/track/{ident}", produces = "audio/mp3")
    public HttpResponse<byte[]> track(@PathVariable final String hoerernummer,
                                      @PathVariable final String titelnummer,
                                      @PathVariable final String ident) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track '{}' mit Wasserzeichen ab",
                hoerernummer, titelnummer, ident);
        try {
            final byte[] track = mp3RepositoryService.track(hoerernummer, titelnummer, ident);
            return HttpResponse.ok(track)
                    .header("Accept-Ranges", "bytes");
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

}
