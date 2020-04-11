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
import lombok.extern.slf4j.Slf4j;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.WatermarkedMp3Service;
import wbh.bookworm.hoerbuchdienst.domain.ports.CatalogService;

@OpenAPIDefinition(
        info = @Info(title = "wbh.sds", version = "0.0")
)
@Controller("/stream")
@Slf4j
public class StreamController {

    private final CatalogService catalogService;

    private final WatermarkedMp3Service watermarkedMp3Service;

    @Inject
    public StreamController(final CatalogService catalogService,
                            final WatermarkedMp3Service watermarkedMp3Service) {
        this.catalogService = catalogService;
        this.watermarkedMp3Service = watermarkedMp3Service;
    }

    @ApiResponse
    @Get(uri = "/{titelnummer}/info", produces = MediaType.APPLICATION_JSON)
    public AudiobookInfoDTO info(@PathVariable final String titelnummer) {
        return catalogService.audiobookInfo(titelnummer);
    }

    @ApiResponse
    @Get(uri = "/{titelnummer}/playlist", produces = MediaType.APPLICATION_JSON)
    public PlaylistDTO playlist(@PathVariable final String titelnummer) {
        return catalogService.playlist(titelnummer);
    }

    @ApiResponse
    @Get(uri = "/{titelnummer}/track/{ident}", produces = "audio/mp3")
    public HttpResponse<byte[]> track(@PathVariable final String titelnummer,
                                      @PathVariable final String ident) {
        log.info("Hörbuch '{}': Rufe Track '{}' mit Wasserzeichen ab", titelnummer, ident);
        return HttpResponse.ok(watermarkedMp3Service.fetch(titelnummer, ident))
                .header("Accept-Ranges", "bytes");
    }

}
