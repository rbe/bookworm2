/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.sharding;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookLocationService;

import aoc.mikrokosmos.crypto.messagedigest.FastByteHash;

@OpenAPIDefinition(
        info = @Info(
                title = "Shard",
                version = "1.0.0",
                description = "Hoerbuchdienst - Shard Redistribution",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(RedistributionController.BASE_URL)
public class RedistributionController {

    static final String BASE_URL = "/v1/shard/redistribute";

    private static final Logger LOGGER = LoggerFactory.getLogger(RedistributionController.class);

    private final AudiobookLocationService audiobookLocationService;

    @Inject
    public RedistributionController(final AudiobookLocationService audiobookLocationService) {
        this.audiobookLocationService = audiobookLocationService;
    }

    @Post(uri = "zip/{titelnummer}/{sHashValue}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
            final Path tempFile;
            try {
                tempFile = Files.createTempFile(getClass().getSimpleName(), ".zip");
                Files.write(tempFile, bytes);
                final CompletionStage<Void> receive = audiobookLocationService.receive(titelnummer,
                        Files.newInputStream(tempFile));
                receive.thenRun(() -> {
                    try {
                        Files.delete(tempFile);
                    } catch (IOException e) {
                        LOGGER.error(String.format("Cannot delete temporary file %s", tempFile), e);
                    }
                });
                LOGGER.info("Started storing audiobook {} in background, {}", titelnummer, receive);
                return HttpResponse.ok(Boolean.TRUE);
            } catch (IOException e) {
                throw new BusinessException("", e);
            }
        } else {
            LOGGER.error("Hash values are not equal, cannot store audiobook {}", titelnummer);
            return HttpResponse.<Boolean>status(HttpStatus.CONFLICT)
                    .body(Boolean.FALSE);
        }
    }

}
