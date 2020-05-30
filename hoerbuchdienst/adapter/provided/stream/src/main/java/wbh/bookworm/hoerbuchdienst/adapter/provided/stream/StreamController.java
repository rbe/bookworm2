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

    private final AudiobookService audiobookService;

    @Inject
    public StreamController(final AudiobookService audiobookService) {
        this.audiobookService = audiobookService;
    }

    @Head(uri = "location/{titelnummer}")
    public HttpResponse<Object> location(@PathVariable final /* TODO AghNummer */String titelnummer) {
        final int shardNumber = audiobookService.shardLocation(titelnummer);
        return HttpResponse.ok().header("X-Shard-Location", String.format("%s", shardNumber));
    }

    @Post(uri = "zip", consumes = MediaType.APPLICATION_JSON, produces = APPLICATION_ZIP)
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

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = AUDIO_MP3)
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
