/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookStreamService;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;

@OpenAPIDefinition()
@Controller(value = StreamController.BASE_URL)
public class StreamController {

    static final String BASE_URL = "stream";

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamController.class);

    private static final String AUDIO_MP3 = "audio/mp3";

    private static final String APPLICATION_ZIP = "application/zip";

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final String EMPTY_STRING = "";

    private final AudiobookShardRedirector audiobookShardRedirector;

    private final AudiobookStreamService audiobookStreamService;

    @Inject
    public StreamController(final AudiobookShardRedirector audiobookShardRedirector,
                            final AudiobookStreamService audiobookStreamService) {
        this.audiobookShardRedirector = audiobookShardRedirector;
        this.audiobookStreamService = audiobookStreamService;
    }

    @Post(uri = "zip", consumes = MediaType.APPLICATION_JSON, produces = APPLICATION_ZIP)
    @Blocking
    public HttpResponse<byte[]> zippedAudiobookAsStream(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        return audiobookShardRedirector.withLocalOrRedirect(audiobookAnfrageDTO.getTitelnummer(),
                () -> makeZippedAudiobook(audiobookAnfrageDTO.getHoerernummer(), audiobookAnfrageDTO.getTitelnummer()),
                HttpResponse::ok,
                EMPTY_BYTE_ARRAY, String.format("%s/zip", BASE_URL));
    }

    private byte[] makeZippedAudiobook(final String hoerernummer, final String titelnummer) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörbuch mit Wasserzeichen als ZIP",
                hoerernummer, titelnummer);
        try (final InputStream audiobook = audiobookStreamService.zipAsStream(hoerernummer,
                titelnummer)) {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Hörbuch mit Wasserzeichen als ZIP erstellt",
                    hoerernummer, titelnummer);
            return audiobook.readAllBytes();
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = AUDIO_MP3)
    @Blocking
    public HttpResponse<byte[]> trackAsStream(@Body final TrackAnfrageDTO trackAnfrageDTO) {
        return audiobookShardRedirector.withLocalOrRedirect(trackAnfrageDTO.getTitelnummer(),
                () -> {
                    LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Track '{}' mit Wasserzeichen",
                            trackAnfrageDTO.getHoerernummer(), trackAnfrageDTO.getTitelnummer(),
                            trackAnfrageDTO.getIdent());
                    try (final InputStream track = audiobookStreamService.trackAsStream(trackAnfrageDTO.getHoerernummer(),
                            trackAnfrageDTO.getTitelnummer(), trackAnfrageDTO.getIdent())) {
                        LOGGER.info("Hörer '{}' Hörbuch '{}': Track '{}' mit Wasserzeichen erstellt",
                                trackAnfrageDTO.getHoerernummer(), trackAnfrageDTO.getTitelnummer(),
                                trackAnfrageDTO.getIdent());
                        return track.readAllBytes();
                    } catch (Exception e) {
                        throw new BusinessException(EMPTY_STRING, e);
                    }
                },
                body -> HttpResponse.ok(body).header("Accept-Ranges", "bytes"),
                EMPTY_BYTE_ARRAY, String.format("%s/track", BASE_URL));
    }

}
