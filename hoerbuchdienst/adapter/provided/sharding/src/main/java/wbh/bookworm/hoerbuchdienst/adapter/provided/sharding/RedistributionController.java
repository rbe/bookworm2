/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.sharding;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookLocationService;

import aoc.mikrokosmos.crypto.messagedigest.FastByteHash;

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

    @Post(uri = "zip/{titelnummer}/{sHashValue}", consumes = MediaType.APPLICATION_OCTET_STREAM,
            produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Boolean> receiveAudiobook(@PathVariable final String titelnummer,
                                                  @PathVariable final String sHashValue,
                                                  @Body final byte[] bytes) {
        final long computedHashValue = FastByteHash.hash(bytes);
        LOGGER.debug("Empfange Hörbuch '{}' als ZIP, {} bytes, hash {}",
                titelnummer, bytes.length, computedHashValue);
        final long hashValue;
        try {
            hashValue = Long.parseLong(sHashValue);
        } catch (NumberFormatException e) {
            throw new BusinessException(String.format("Cannot convert hash value %s to number", sHashValue), e);
        }
        if (computedHashValue == hashValue) {
            LOGGER.debug("Hash values are equal, start storing audiobook {}", titelnummer);
            final CompletionStage<Boolean> receive = audiobookLocationService
                    .receive(titelnummer, bytes, hashValue);
            LOGGER.info("Started storing audiobook {} in background, {}", titelnummer, receive);
            return HttpResponse.ok(Boolean.TRUE);
        } else {
            LOGGER.error("Hash values are not equal, cannot store audiobook {}", titelnummer);
            return HttpResponse.<Boolean>status(HttpStatus.CONFLICT)
                    .body(Boolean.FALSE);
        }
    }

}
