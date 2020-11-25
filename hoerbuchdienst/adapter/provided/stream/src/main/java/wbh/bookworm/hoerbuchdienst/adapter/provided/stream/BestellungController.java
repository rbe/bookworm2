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

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
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
@Controller(value = BestellungController.BASE_URL,
        consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class BestellungController {

    static final String BASE_URL = "/v1/bestellung";

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellungController.class);

    private static final String APPLICATION_ZIP = "application/zip";

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
    @Options(uri = "zip")
    public HttpResponse<String> optionsOrderZippedAudiobook(final HttpRequest<?> httpRequest) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "DAISY-ZIP mit Wasserzeichen bestellen")
    @Post(uri = "zip")
    public HttpResponse<Map<String, String>> orderZippedAudiobook(final HttpRequest<?> httpRequest,
                                                                  @Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        return audiobookShardRedirector.withLocalOrRedirect(audiobookAnfrageDTO.getTitelnummer(),
                () -> {
                    final UUID orderId = UUID.randomUUID();
                    audiobookOrderService.orderZip(audiobookAnfrageDTO.getMandant(), audiobookAnfrageDTO.getHoerernummer(),
                            audiobookAnfrageDTO.getTitelnummer(), orderId.toString());
                    LOGGER.info("Hörer '{}' Hörbuch '{}': Bestellung aufgegeben",
                            audiobookAnfrageDTO.getHoerernummer(), audiobookAnfrageDTO.getTitelnummer());
                    return Map.of("orderId", orderId.toString());
                },
                body -> CORS.response(httpRequest, body),
                String.format("%s/zip", BASE_URL),
                httpRequest);
    }

    @Operation(hidden = true)
    @Options(uri = "zip/{titelnummer}/status/{orderId}")
    public HttpResponse<String> optionsFetchStatusOfZippedAudiobook(final HttpRequest<?> httpRequest,
                                                                    @PathVariable final String titelnummer,
                                                                    @PathVariable final String orderId) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Status einer Bestellung DAISY-ZIP mit Wasserzeichen abrufen")
    @Get(uri = "zip/{titelnummer}/status/{orderId}", headRoute = false)
    public HttpResponse<Map<String, String>> fetchStatusOfZippedAudiobook(final HttpRequest<?> httpRequest,
                                                                          @PathVariable final String titelnummer,
                                                                          @PathVariable final String orderId) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    final String status = audiobookOrderService.orderStatus(orderId);
                    LOGGER.info("Hörbuch {}: Status der Bestellung {} ist {}", titelnummer, orderId, status);
                    return Map.of("orderStatus", status);
                },
                body -> CORS.response(httpRequest, body),
                String.format("%s/zip/%s/status/%s", BASE_URL, titelnummer, orderId),
                httpRequest);
    }

    @Operation(hidden = true)
    @Options(uri = "zip/{titelnummer}/fetch/{orderId}")
    public HttpResponse<String> optionsFetchZippedAudiobook(final HttpRequest<?> httpRequest,
                                                            @PathVariable final String titelnummer,
                                                            @PathVariable final String orderId) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "DAISY-ZIP mit Wasserzeichen abholen")
    @Get(uri = "zip/{titelnummer}/fetch/{orderId}", headRoute = false, produces = APPLICATION_ZIP)
    public HttpResponse<byte[]> fetchZippedAudiobook(final HttpRequest<?> httpRequest,
                                                     @PathVariable final String titelnummer,
                                                     @PathVariable final String orderId) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    LOGGER.info("Hörbuch {}: Bestellung {} wird abgeholt", titelnummer, orderId);
                    try (final InputStream inputStream = audiobookOrderService.fetchOrder(orderId)) {
                        return inputStream.readAllBytes();
                    } catch (IOException e) {
                        throw new BusinessException(EMPTY_STRING, e);
                    }
                },
                body -> CORS.response(httpRequest, body)
                        .header("Content-Disposition", String.format("inline; filename=\"%s.zip\"", titelnummer)),
                String.format("%s/zip/%s/fetch/%s", BASE_URL, titelnummer, orderId),
                httpRequest);
    }

}
