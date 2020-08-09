/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.sharding;

import javax.inject.Inject;
import java.io.InputStream;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookLocationService;

@Controller(RedistributionController.BASE_URL)
public class RedistributionController {

    static final String BASE_URL = "shard/redistribute";

    private static final Logger LOGGER = LoggerFactory.getLogger(RedistributionController.class);

    private static final String APPLICATION_ZIP = "application/zip";
    //private static final MediaType APPLICATION_ZIP=MediaType.of("application/zip");

    private final AudiobookLocationService audiobookLocationService;

    @Inject
    public RedistributionController(final AudiobookLocationService audiobookLocationService) {
        this.audiobookLocationService = audiobookLocationService;
    }

    @Post(uri = "zip/{titelnummer}/{hashValue}", consumes = MediaType.APPLICATION_OCTET_STREAM, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Boolean> audiobook(@PathVariable final String titelnummer,
                                           @PathVariable final String hashValue,
                                           @Body InputStream inputStream) {
        LOGGER.debug("Empfange Hörbuch '{}' als ZIP", titelnummer);
        final boolean objectReceived = audiobookLocationService.receiveObject(titelnummer, inputStream, hashValue);
        if (objectReceived) {
            return HttpResponse.ok(Boolean.TRUE);
        } else {
            return HttpResponse.<Boolean>status(HttpStatus.CONFLICT)
                    .body(Boolean.FALSE);
        }
    }

}
