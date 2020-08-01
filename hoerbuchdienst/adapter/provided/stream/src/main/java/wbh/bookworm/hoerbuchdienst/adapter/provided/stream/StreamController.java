/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Head;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookService;

@Controller("/stream")
public class StreamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamController.class);

    private static final String AUDIO_MP3 = "audio/mp3";

    private static final String APPLICATION_ZIP = "application/zip";

    private static final String X_SHARD_LOCATION = "X-Shard-Location";

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final String EMPTY_STRING = "";

    private final AudiobookService audiobookService;

    @Inject
    public StreamController(final AudiobookService audiobookService) {
        this.audiobookService = audiobookService;
    }

    @Head(uri = "location/{titelnummer}")
    public HttpResponse<Object> location(@PathVariable final /* TODO Mandantenspezifisch */String titelnummer) {
        final String shardName = audiobookService.shardLocation(titelnummer);
        return HttpResponse.ok().header(X_SHARD_LOCATION, shardName);
    }

    @Post(uri = "zip/sync", consumes = MediaType.APPLICATION_JSON, produces = APPLICATION_ZIP)
    @Blocking
    public HttpResponse<byte[]> syncZippedAudiobookAsStream(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        final boolean locatedLocal = audiobookService.locatedLocal(audiobookAnfrageDTO.getTitelnummer());
        if (locatedLocal) {
            return HttpResponse.ok(makeZippedAudiobook(audiobookAnfrageDTO.getHoerernummer(),
                    audiobookAnfrageDTO.getTitelnummer()));
        } else {
            return tryRedirectToOwningShard(audiobookAnfrageDTO.getHoerernummer(),
                    audiobookAnfrageDTO.getTitelnummer(), EMPTY_BYTE_ARRAY, "stream/zip/sync");
        }
    }

    private byte[] makeZippedAudiobook(final String hoerernummer, final String titelnummer) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörbuch mit Wasserzeichen als ZIP",
                hoerernummer, titelnummer);
        try (final InputStream audiobook = audiobookService.zipAsStream(hoerernummer,
                titelnummer)) {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Hörbuch mit Wasserzeichen als ZIP erstellt",
                    hoerernummer, titelnummer);
            return audiobook.readAllBytes();
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

    private <T> HttpResponse<T> tryRedirectToOwningShard(final String hoerernummer, final String titelnummer,
                                                         final T emptyResponseBody, final String uri) {
        final String shardName = audiobookService.shardLocation(titelnummer);
        if (shardName.equals("unknown")) {
            return HttpResponse.notFound();
        } else {
            final String shardURI = String.format("https://%s/%s", shardName, uri);
            LOGGER.debug("Hörer '{}' Hörbuch '{}': Redirecting to {}", hoerernummer, titelnummer, shardURI);
            return HttpResponse.<T>temporaryRedirect(URI.create(shardURI))
                    .header(X_SHARD_LOCATION, shardName)
                    .body(emptyResponseBody);
        }
    }

    @Post(uri = "zip/order", consumes = MediaType.APPLICATION_JSON, produces = APPLICATION_ZIP)
    public HttpResponse<String> zippedAudiobookAsStream(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        final boolean locatedLocal = audiobookService.locatedLocal(audiobookAnfrageDTO.getTitelnummer());
        if (locatedLocal) {
            final UUID orderId = UUID.randomUUID();
            audiobookService.orderZip(audiobookAnfrageDTO.getHoerernummer(), audiobookAnfrageDTO.getTitelnummer(),
                    orderId.toString());
            LOGGER.info("Hörer '{}' Hörbuch '{}': Bestellung aufgegeben",
                    audiobookAnfrageDTO.getHoerernummer(), audiobookAnfrageDTO.getTitelnummer());
            return HttpResponse.ok(orderId.toString());
        } else {
            return tryRedirectToOwningShard(audiobookAnfrageDTO.getHoerernummer(),
                    audiobookAnfrageDTO.getTitelnummer(), EMPTY_STRING, "stream/zip/order");
        }
    }

    @Get(uri = "zip/status/{orderId}", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<String> fetchStatusOfZippedAudiobook(@PathVariable final String orderId) {
        final String status = audiobookService.orderStatus(orderId);
        LOGGER.info("Hörer '{}' Hörbuch '{}': Status der Bestellung {} ist {}", orderId, status);
        return HttpResponse.ok(status);
    }

    @Get(uri = "zip/fetch/{orderId}", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<byte[]> fetchZippedAudiobook(@PathVariable final String orderId) {
        LOGGER.info("Hörer '{}' Hörbuch '{}': Bestellung {} wird abgeholt", orderId);
        try (final InputStream inputStream = audiobookService.fetchOrder(orderId)) {
            return HttpResponse.ok(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = AUDIO_MP3)
    @Blocking
    public HttpResponse<byte[]> trackAsStream(@Body final TrackAnfrageDTO trackAnfrageDTO) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Track '{}' mit Wasserzeichen",
                trackAnfrageDTO.getHoerernummer(), trackAnfrageDTO.getTitelnummer(),
                trackAnfrageDTO.getIdent());
        try (final InputStream track = audiobookService.trackAsStream(trackAnfrageDTO.getHoerernummer(),
                trackAnfrageDTO.getTitelnummer(), trackAnfrageDTO.getIdent())) {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Track '{}' mit Wasserzeichen erstellt",
                    trackAnfrageDTO.getHoerernummer(), trackAnfrageDTO.getTitelnummer(),
                    trackAnfrageDTO.getIdent());
            return HttpResponse.ok(track.readAllBytes())
                    .header("Accept-Ranges", "bytes");
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

}
