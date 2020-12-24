/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
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
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookOrderService;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

import static wbh.bookworm.hoerbuchdienst.sharding.shared.CORS.optionsResponse;

@OpenAPIDefinition(
        info = @Info(
                title = "Bestellung",
                version = "1.0.0",
                description = "Hoerbuchdienst - Hörbucher bestellen",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(value = BestellungController.BASE_URL)
public class BestellungController {

    static final String BASE_URL = "/v1/bestellung";

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellungController.class);

    private static final String APPLICATION_ZIP_VALUE = "application/zip";

    private static final MediaType APPLICATION_ZIP = MediaType.of("application/zip");

    private static final String EMPTY_STRING = "";

    private final AudiobookOrderService audiobookOrderService;

    private final AudiobookShardRedirector audiobookShardRedirector;

    @Inject
    public BestellungController(final AudiobookOrderService audiobookOrderService,
                                final AudiobookShardRedirector audiobookShardRedirector) {
        this.audiobookOrderService = audiobookOrderService;
        this.audiobookShardRedirector = audiobookShardRedirector;
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}")
    public HttpResponse<String> optionsOrderZippedAudiobook(final HttpRequest<?> httpRequest,
                                                            @PathVariable final String titelnummer) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Hörbuch als DAISY-ZIP bestellen")
    @Post(uri = "/{titelnummer}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Map<String, String>> orderZippedAudiobook(final HttpRequest<?> httpRequest,
                                                                  @Header("X-Bookworm-Mandant") final String xMandant,
                                                                  @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                  @PathVariable final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    final UUID orderId = UUID.randomUUID();
                    audiobookOrderService.orderZip(xMandant, xHoerernummer, titelnummer, orderId.toString());
                    LOGGER.info("Hörer '{}' Hörbuch '{}': Bestellung aufgegeben", xHoerernummer, titelnummer);
                    return Map.of("orderId", orderId.toString());
                },
                body -> CORS.response(httpRequest, body),
                String.format("%s/%s", BASE_URL, titelnummer),
                httpRequest);
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}/status/{orderId}")
    public HttpResponse<String> optionsFetchStatusOfZippedAudiobook(final HttpRequest<?> httpRequest,
                                                                    @PathVariable final String titelnummer,
                                                                    @PathVariable final String orderId) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Status einer Bestellung DAISY-ZIP abrufen")
    @Get(uri = "/{titelnummer}/status/{orderId}", headRoute = false)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<Map<String, String>> fetchStatusOfZippedAudiobook(final HttpRequest<?> httpRequest,
                                                                          @Header("X-Bookworm-Mandant") final String xMandant,
                                                                          @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                          @PathVariable final String titelnummer,
                                                                          @PathVariable final String orderId) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    final String status = audiobookOrderService.orderStatus(orderId);
                    LOGGER.info("Hörbuch {}: Status der Bestellung {} ist {}", titelnummer, orderId, status);
                    return Map.of("orderStatus", status);
                },
                body -> CORS.response(httpRequest, body),
                String.format("%s/%s/status/%s", BASE_URL, titelnummer, orderId),
                httpRequest);
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}/fetch/{orderId}")
    public HttpResponse<String> optionsFetchZippedAudiobook(final HttpRequest<?> httpRequest,
                                                            @PathVariable final String titelnummer,
                                                            @PathVariable final String orderId) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Bestellung DAISY-ZIP abholen")
    @Get(uri = "/{titelnummer}/fetch/{orderId}", headRoute = false, produces = APPLICATION_ZIP_VALUE)
    @Produces(APPLICATION_ZIP_VALUE)
    @Blocking
    public HttpResponse<StreamedFile> fetchZippedAudiobook(final HttpRequest<?> httpRequest,
                                                           @PathVariable final String titelnummer,
                                                           @PathVariable final String orderId) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    LOGGER.info("Hörbuch {}: Bestellung {} wird abgeholt", titelnummer, orderId);
                    try (final InputStream inputStream = audiobookOrderService.fetchOrder(orderId)) {
                        return new StreamedFile(inputStream, APPLICATION_ZIP);
                    } catch (IOException e) {
                        throw new BusinessException(EMPTY_STRING, e);
                    }
                },
                body -> CORS.response(httpRequest, body)
                        .contentType(APPLICATION_ZIP_VALUE)
                        .contentLength(body.getLength())
                        .header("Content-Disposition", String.format("attachment; filename=\"%s.zip\"", titelnummer)),
                String.format("%s/%s/fetch/%s", BASE_URL, titelnummer, orderId),
                httpRequest);
    }

}
