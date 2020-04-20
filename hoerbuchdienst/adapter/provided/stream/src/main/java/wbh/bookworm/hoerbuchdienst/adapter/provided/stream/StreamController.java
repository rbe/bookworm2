/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.Mp3RepositoryService;

@Controller("/stream")
public class StreamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamController.class);

    private final Mp3RepositoryService mp3RepositoryService;

    @Inject
    public StreamController(final Mp3RepositoryService mp3RepositoryService) {
        this.mp3RepositoryService = mp3RepositoryService;
    }

    @Post(uri = "zip", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_OCTET_STREAM)
    public HttpResponse<byte[]> audiobook(@Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        throw new BusinessException("Noch nicht implementiert");
    }

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = "audio/mp3")
    public HttpResponse<byte[]> track(@Body final TrackAnfrageDTO trackAnfrageDTO) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track '{}' mit Wasserzeichen ab",
                trackAnfrageDTO.getHoerernummer(), trackAnfrageDTO.getTitelnummer(),
                trackAnfrageDTO.getIdent());
        try {
            final byte[] track = mp3RepositoryService.track(trackAnfrageDTO.getHoerernummer(),
                    trackAnfrageDTO.getTitelnummer(), trackAnfrageDTO.getIdent());
            return HttpResponse.ok(track)
                    .header("Accept-Ranges", "bytes");
        } catch (Exception e) {
            throw new BusinessException("", e);
        }
    }

}
