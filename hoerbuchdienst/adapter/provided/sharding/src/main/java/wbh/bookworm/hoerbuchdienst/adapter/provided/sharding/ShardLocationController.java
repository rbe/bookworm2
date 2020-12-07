/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.sharding;

import javax.inject.Inject;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Head;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookLocationService;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

import static wbh.bookworm.hoerbuchdienst.sharding.shared.CORS.optionsResponse;

@OpenAPIDefinition(
        info = @Info(
                title = "Shard",
                version = "1.0.0",
                description = "Hoerbuchdienst - Shard Information",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(ShardLocationController.SHARD)
public class ShardLocationController {

    static final String SHARD = "/v1/shard/location";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardLocationController.class);

    private static final String X_SHARD_LOCATION = "X-Bookworm-ShardLocation";

    private final AudiobookLocationService audiobookLocationService;

    @Inject
    public ShardLocationController(final AudiobookLocationService audiobookLocationService) {
        this.audiobookLocationService = audiobookLocationService;
    }

    @Operation(hidden = true)
    @Options(uri = "/{objectId}")
    public HttpResponse<String> optionsLocation(final HttpRequest<?> httpRequest,
                                                @PathVariable final String objectId) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Ort/Shard eines Hörbuchs abfragen")
    @Head(uri = "/{objectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<String> location(final HttpRequest<?> httpRequest,
                                         @PathVariable final String objectId) {
        final String shardName = audiobookLocationService.shardLocation(objectId);
        LOGGER.info("Shard for object {} is {}", objectId, shardName);
        return CORS.response(httpRequest, "")
                .header(X_SHARD_LOCATION, shardName);
    }

}
