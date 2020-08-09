/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookOrderService;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;

@OpenAPIDefinition()
@Controller(value = BestellungController.BASE_URL)
public class BestellungController {

    static final String BASE_URL = "bestellung";

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellungController.class);

    private static final String APPLICATION_ZIP = "application/zip";

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final String EMPTY_STRING = "";

    private final AudiobookOrderService audiobookOrderService;

    private final AudiobookShardRedirector audiobookShardRedirector;

    @Inject
    public BestellungController(final AudiobookOrderService audiobookOrderService,
                                final AudiobookShardRedirector audiobookShardRedirector) {
        this.audiobookOrderService = audiobookOrderService;
        this.audiobookShardRedirector = audiobookShardRedirector;
    }

    @Post(uri = "zip", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<String> orderZippedAudiobook(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        return audiobookShardRedirector.withLocalOrRedirect(audiobookAnfrageDTO.getTitelnummer(),
                () -> {
                    final UUID orderId = UUID.randomUUID();
                    audiobookOrderService.orderZip(audiobookAnfrageDTO.getHoerernummer(),
                            audiobookAnfrageDTO.getTitelnummer(), orderId.toString());
                    LOGGER.info("Hörer '{}' Hörbuch '{}': Bestellung aufgegeben",
                            audiobookAnfrageDTO.getHoerernummer(), audiobookAnfrageDTO.getTitelnummer());
                    return orderId.toString();
                }, body -> HttpResponse.ok(body),
                EMPTY_STRING, String.format("%s/zip/order", BASE_URL));
    }

    @Get(uri = "zip/{titelnummer}/status/{orderId}", headRoute = false, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<String> fetchStatusOfZippedAudiobook(@PathVariable final String titelnummer,
                                                             @PathVariable final String orderId) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> {
                    final String status = audiobookOrderService.orderStatus(orderId);
                    LOGGER.info("Hörbuch {}: Status der Bestellung {} ist {}", titelnummer, orderId, status);
                    return status;
                }, body -> HttpResponse.ok(body),
                EMPTY_STRING, String.format("%s/zip/%s/status/%s", BASE_URL, titelnummer, orderId));
    }

    @Get(uri = "zip/{titelnummer}/fetch/{orderId}", headRoute = false, produces = APPLICATION_ZIP)
    public HttpResponse<byte[]> fetchZippedAudiobook(@PathVariable final String titelnummer,
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
                body -> HttpResponse.ok(body)
                        .header("Content-Disposition", String.format("inline; filename=\"%s.zip\"", titelnummer)),
                EMPTY_BYTE_ARRAY, String.format("%s/zip/%s/fetch/%s", BASE_URL, titelnummer, orderId));
    }

}
