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

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.HoerbuchNichtGefundenException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
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
@Controller(HoerbuchController.BASE_URL)
public class HoerbuchController {

    static final String BASE_URL = "/v1/katalog";

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchController.class);

    private final KatalogService katalogService;

    private final AudiobookShardRedirector audiobookShardRedirector;

    @Inject
    public HoerbuchController(final KatalogService katalogService,
                              final AudiobookShardRedirector audiobookShardRedirector) {
        this.katalogService = katalogService;
        this.audiobookShardRedirector = audiobookShardRedirector;
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}")
    public HttpResponse<String> optionsAudiobookInfo(final HttpRequest<?> httpRequest,
                                                     @PathVariable final String titelnummer) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Informationen über Hörbuch abfragen")
    @Get(uri = "/{titelnummer}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<AudiobookInfoAntwortDTO> audiobookInfo(final HttpRequest<?> httpRequest,
                                                               @Header("X-Bookworm-Mandant") final String xMandant,
                                                               @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                               @PathVariable final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    try {
                        final AudiobookInfoDTO audiobookInfoDTO = katalogService.audiobookInfo(titelnummer);
                        return AudiobookMapper.INSTANCE.convert(audiobookInfoDTO);
                    } catch (Exception e) {
                        throw new HoerbuchNichtGefundenException(String.format("Hörbuch %s nicht gefunden", titelnummer), e);
                    }
                },
                dto -> CORS.response(httpRequest, dto),
                String.format("%s/%s", BASE_URL, titelnummer),
                httpRequest);
    }

    @Mapper
    public interface AudiobookMapper {

        AudiobookMapper INSTANCE = Mappers.getMapper(AudiobookMapper.class);

        AudiobookInfoAntwortDTO convert(AudiobookInfoDTO audiobookInfoDTO);

    }

}
