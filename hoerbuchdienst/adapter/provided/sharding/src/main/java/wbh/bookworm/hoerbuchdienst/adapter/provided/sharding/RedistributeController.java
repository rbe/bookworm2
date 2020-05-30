/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.sharding;

import javax.inject.Inject;
import java.io.InputStream;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookService;

@Controller("/sharding")
public class RedistributeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedistributeController.class);

    private final AudiobookService audiobookService;

    @Inject
    public RedistributeController(final AudiobookService audiobookService) {
        this.audiobookService = audiobookService;
    }

    @Post(uri = "zip/{titelnummer}", consumes = MediaType.APPLICATION_OCTET_STREAM, produces = MediaType.APPLICATION_OCTET_STREAM)
    public HttpResponse<Boolean> audiobook(@PathVariable final String titelnummer, @Body final InputStream inputStream) {
        LOGGER.debug("Empfange Hörbuch '{}' als ZIP", titelnummer);
        if (audiobookService.putZip(inputStream, titelnummer)) {
            return HttpResponse.ok(Boolean.TRUE);
        } else {
            return HttpResponse.ok(Boolean.FALSE);
        }
    }

}
