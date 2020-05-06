/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookService;

@Controller("/stream")
public class StreamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamController.class);

    private final AudiobookService audiobookService;

    @Inject
    public StreamController(final AudiobookService audiobookService) {
        this.audiobookService = audiobookService;
    }

    @Post(uri = "zip", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_OCTET_STREAM)
    public HttpResponse<byte[]> audiobook(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Hörbuch mit Wasserzeichen als ZIP ab",
                audiobookAnfrageDTO.getHoerernummer(), audiobookAnfrageDTO.getTitelnummer());
        try (final InputStream audiobook = audiobookService.zipAsStream(audiobookAnfrageDTO.getHoerernummer(),
                audiobookAnfrageDTO.getTitelnummer())) {
            return HttpResponse.ok(audiobook.readAllBytes());
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = "audio/mp3")
    public HttpResponse<byte[]> track(@Body final TrackAnfrageDTO trackAnfrageDTO) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track '{}' mit Wasserzeichen ab",
                trackAnfrageDTO.getHoerernummer(), trackAnfrageDTO.getTitelnummer(),
                trackAnfrageDTO.getIdent());
        try (final InputStream track = audiobookService.trackAsStream(trackAnfrageDTO.getHoerernummer(),
                trackAnfrageDTO.getTitelnummer(), trackAnfrageDTO.getIdent())) {
            return HttpResponse.ok(track.readAllBytes())
                    .header("Accept-Ranges", "bytes");
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

}
