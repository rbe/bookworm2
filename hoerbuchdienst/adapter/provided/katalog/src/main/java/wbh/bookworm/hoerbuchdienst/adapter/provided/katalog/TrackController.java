/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.katalog;

import javax.inject.Inject;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.TrackInfoDTO;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

import static wbh.bookworm.hoerbuchdienst.sharding.shared.CORS.optionsResponse;

@OpenAPIDefinition(
        info = @Info(
                title = "Katalog",
                version = "1.0.0",
                description = "Hoerbuchdienst - Katalog",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(TrackController.BASE_URL)
public class TrackController {

    static final String BASE_URL = "/v1/katalog";

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

    private final KatalogService katalogService;

    private final AudiobookShardRedirector audiobookShardRedirector;

    @Inject
    public TrackController(final KatalogService katalogService,
                           final AudiobookShardRedirector audiobookShardRedirector) {
        this.katalogService = katalogService;
        this.audiobookShardRedirector = audiobookShardRedirector;
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}/track/{ident}")
    public HttpResponse<String> optionsTrack(final HttpRequest<?> httpRequest,
                                             @PathVariable final String titelnummer,
                                             @PathVariable final String ident) {
        return optionsResponse(httpRequest);
    }

    @Get(uri = "/{titelnummer}/track/{ident}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<TrackInfoAntwortDTO> track(final HttpRequest<?> httpRequest,
                                                   @Header("X-Bookworm-Mandant") final String xMandant,
                                                   @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                   @PathVariable final String titelnummer,
                                                   @PathVariable final String ident) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track-Info '{}' mit Wasserzeichen ab",
                            xHoerernummer, titelnummer, ident);
                    try {
                        final TrackInfoDTO trackInfoDTO = katalogService.trackInfo(xHoerernummer, titelnummer, ident);
                        return TrackMapper.INSTANCE.convert(trackInfoDTO);
                    } catch (Exception e) {
                        throw new BusinessException("", e);
                    }
                },
                dto -> CORS.response(httpRequest, dto),
                String.format("%s/%s/track/%s", BASE_URL, titelnummer, ident),
                httpRequest);
    }

    @Mapper
    public interface TrackMapper {

        TrackMapper INSTANCE = Mappers.getMapper(TrackMapper.class);

        TrackInfoAntwortDTO convert(TrackInfoDTO trackInfoDTO);

    }

}
