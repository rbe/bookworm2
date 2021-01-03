/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Optional;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.types.files.StreamedFile;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.HoerprobeService;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

@OpenAPIDefinition(
        info = @Info(
                title = "Bestellung",
                version = "1.0.0",
                description = "Hoerbuchdienst - Hörbücher bestellen",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(value = HoerprobeController.BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(HoerprobeController.AUDIO_MP3_VALUE)
public class HoerprobeController {

    static final String BASE_URL = "/v1/hoerprobe";

    static final String AUDIO_MP3_VALUE = "audio/mp3";

    static final MediaType AUDIO_MP3 = MediaType.of(AUDIO_MP3_VALUE);

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerprobeController.class);

    private final AudiobookShardRedirector audiobookShardRedirector;

    private final HoerprobeService hoerprobeService;

    @Inject
    public HoerprobeController(final AudiobookShardRedirector audiobookShardRedirector,
                               final HoerprobeService hoerprobeService) {
        this.audiobookShardRedirector = audiobookShardRedirector;
        this.hoerprobeService = hoerprobeService;
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}")
    public MutableHttpResponse<String> optionsHoerprobeAsStream(final HttpRequest<?> httpRequest,
                                                                @PathVariable("titelnummer") final String titelnummer) {
        return CORS.optionsResponse(httpRequest);
    }

    @Operation(summary = "Hörprobe eines Hörbuchs abrufen")
    @Get(uri = "/{titelnummer}")
    @Blocking
    public HttpResponse<StreamedFile> hoerprobeAsStream(final HttpRequest<?> httpRequest,
                                                        /*
                                                        @Header("X-Bookworm-Mandant") final String xMandant,
                                                        @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                        */
                                                        @PathVariable("titelnummer") final String titelnummer) {
        final String xMandant = "06";
        final String xHoerernummer = "00000";
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    final Optional<InputStream> maybeInputStream = hoerprobeService.makeHoerprobeAsStream(xMandant, xHoerernummer, titelnummer);
                    final InputStream inputStream = maybeInputStream.orElseThrow(() -> new BusinessException("Keine Hörprobe möglich"));
                    return new StreamedFile(inputStream, AUDIO_MP3);
                },
                body -> CORS.response(httpRequest, body)
                        .header("Accept-Ranges", "bytes"),
                String.format("%s/%s", BASE_URL, titelnummer),
                httpRequest);
    }

}
