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

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookLocationService;

@Controller(ReshardingController.BASE_URL)
public class ReshardingController {

    static final String BASE_URL = "shard";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReshardingController.class);

    private final AudiobookLocationService audiobookLocationService;

    @Inject
    public ReshardingController(final AudiobookLocationService audiobookLocationService) {
        this.audiobookLocationService = audiobookLocationService;
    }

    // shard receives object (push, by REST endpoint)
    @Post(uri = "zip/{titelnummer}/{hash}", consumes = MediaType.APPLICATION_OCTET_STREAM, produces = MediaType.APPLICATION_OCTET_STREAM)
    public HttpResponse<Boolean> audiobook(@PathVariable final String titelnummer,
                                           @PathVariable final String hash,
                                           @Body final InputStream inputStream) {
        LOGGER.debug("Empfange Hörbuch '{}' als ZIP", titelnummer);
        if (1 == 2 /* TODO Domäne muss etwas anbieten *//*audiobook???Service.receiveObject(titelnummer, inputStream, hash)*/) {
            // TODO check if object was received and stored successfully (compare computed with received hash)
            return HttpResponse.ok(Boolean.TRUE);
        } else {
            return HttpResponse.ok(Boolean.FALSE);
        }
    }

}
