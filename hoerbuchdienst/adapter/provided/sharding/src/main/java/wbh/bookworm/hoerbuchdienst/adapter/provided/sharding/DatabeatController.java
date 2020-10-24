/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.sharding;

import javax.inject.Inject;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.ShardService;

@Controller(DatabeatController.BASE_URL)
public class DatabeatController {

    static final String BASE_URL = "shard/databeat";

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatController.class);

    private final ShardService shardService;

    @Inject
    public DatabeatController(final ShardService shardService) {
        this.shardService = shardService;
    }

    @Get(uri = "generate", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Boolean> generateDatabeat() {
        LOGGER.info("Databeat generation requested");
        shardService.generateDatabeat();
        return HttpResponse.ok(true);
    }

}
