/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.sharding;

import javax.inject.Inject;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Head;
import io.micronaut.http.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookLocationService;

@Controller(ShardInfoController.SHARD)
public class ShardInfoController {

    static final String SHARD = "shard";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardInfoController.class);

    private static final String X_SHARD_LOCATION = "X-Shard-Location";

    private final AudiobookLocationService audiobookLocationService;

    @Inject
    public ShardInfoController(final AudiobookLocationService audiobookLocationService) {
        this.audiobookLocationService = audiobookLocationService;
    }

    @Head(uri = "location/{objectId}")
    public HttpResponse<Object> location(@PathVariable final String objectId) {
        final String shardName = audiobookLocationService.shardLocation(objectId);
        LOGGER.info("Shard for object {} is {}", objectId, shardName);
        return HttpResponse.ok().header(X_SHARD_LOCATION, shardName);
    }

}
