/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
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
@Controller(value = TrackController.BASE_URL)
public class TrackController {

    static final String BASE_URL = "/v1/stream";

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackController.class);

    private static final String AUDIO_MP3 = "audio/mp3";

    private static final String APPLICATION_ZIP = "application/zip";
    //private static final MediaType APPLICATION_ZIP=MediaType.of("application/zip");

    private static final String EMPTY_STRING = "";

    private final AudiobookShardRedirector audiobookShardRedirector;

    private final AudiobookStreamService audiobookStreamService;

    @Inject
    public TrackController(final AudiobookShardRedirector audiobookShardRedirector,
                           final AudiobookStreamService audiobookStreamService) {
        this.audiobookShardRedirector = audiobookShardRedirector;
        this.audiobookStreamService = audiobookStreamService;
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}/track/{ident}")
    public HttpResponse<String> optionsTrackAsStream(final HttpRequest<?> httpRequest,
                                                     @PathVariable final String titelnummer,
                                                     @PathVariable final String ident) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Track eines Hörbuchs als DAISY-ZIP")
    @Get(uri = "/{titelnummer}/track/{ident}", consumes = MediaType.APPLICATION_JSON, produces = AUDIO_MP3)
    @Blocking
    public HttpResponse<byte[]> trackAsStream(final HttpRequest<?> httpRequest,
                                              @Header("X-Bookworm-Mandant") final String xMandant,
                                              @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                              @PathVariable("titelnummer") final String titelnummer,
                                              @PathVariable("ident") final String trackIdent) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeTrackAsStream(xMandant, xHoerernummer, titelnummer, trackIdent),
                body -> CORS.response(httpRequest, body)
                        .header("Accept-Ranges", "bytes"),
                String.format("%s/%s/track/%s", BASE_URL, titelnummer, trackIdent),
                httpRequest);
    }

    private byte[] makeTrackAsStream(final String xMandant, final String xHoerernummer,
                                     final String titelnummer, final String trackIdent) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Track '{}' mit Wasserzeichen",
                xHoerernummer, titelnummer, trackIdent);
        try (final InputStream track = audiobookStreamService.trackAsStream(xMandant,
                xHoerernummer, titelnummer, trackIdent)) {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Track '{}' mit Wasserzeichen erstellt",
                    xHoerernummer, titelnummer, trackIdent);
            return track.readAllBytes();
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

}
