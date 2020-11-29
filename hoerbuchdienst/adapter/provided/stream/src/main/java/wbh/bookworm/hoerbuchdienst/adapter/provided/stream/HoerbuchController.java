/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookStreamService;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

import static wbh.bookworm.hoerbuchdienst.sharding.shared.CORS.optionsResponse;

@OpenAPIDefinition(
        info = @Info(
                title = "Bestellung",
                version = "1.0.0",
                description = "Hoerbuchdienst - Hörbücher bestellen",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(value = HoerbuchController.BASE_URL)
public class HoerbuchController {

    static final String BASE_URL = "/v1/hoerbuch";

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchController.class);

    private static final String APPLICATION_ZIP = "application/zip";
    //private static final MediaType APPLICATION_ZIP=MediaType.of("application/zip");

    private static final String EMPTY_STRING = "";

    private final AudiobookShardRedirector audiobookShardRedirector;

    private final AudiobookStreamService audiobookStreamService;

    @Inject
    public HoerbuchController(final AudiobookShardRedirector audiobookShardRedirector,
                              final AudiobookStreamService audiobookStreamService) {
        this.audiobookShardRedirector = audiobookShardRedirector;
        this.audiobookStreamService = audiobookStreamService;
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}")
    public HttpResponse<String> optionsZippedAudiobookByTitelnummerAsStream(final HttpRequest<?> httpRequest,
                                                                            @PathVariable final String titelnummer) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Hörbuch (Titelnummer) als DAISY-ZIP")
    @ApiResponse(responseCode = "200", description = "DAISY-ZIP wird als Stream geliefert")
    @Post(uri = "/{titelnummer}", consumes = MediaType.APPLICATION_JSON, produces = APPLICATION_ZIP)
    @Blocking
    public HttpResponse<byte[]> zippedAudiobookByTitelnummerAsStream(final HttpRequest<?> httpRequest,
                                                                     @Header("X-Bookworm-Mandant") final String xMandant,
                                                                     @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                     @PathVariable("titelnummer") final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeZippedAudiobook(xMandant, xHoerernummer, titelnummer),
                dto -> CORS.response(httpRequest, dto),
                String.format("%s/%s", BASE_URL, titelnummer),
                httpRequest);
    }

    private byte[] makeZippedAudiobook(final String mandant, final String hoerernummer, final String titelnummer) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörbuch mit Wasserzeichen als ZIP",
                hoerernummer, titelnummer);
        final Path zip = audiobookStreamService.zipAsFile(mandant, hoerernummer, titelnummer);
        try {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Hörbuch mit Wasserzeichen als ZIP erstellt",
                    hoerernummer, titelnummer);
            final byte[] bytes = Files.readAllBytes(zip);
            Files.delete(zip);
            return bytes;
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

}
