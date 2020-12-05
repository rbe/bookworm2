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
import io.micronaut.http.annotation.Produces;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.ShardService;

@OpenAPIDefinition(
        info = @Info(
                title = "Shard",
                version = "1.0.0",
                description = "Hoerbuchdienst - Shard Databeat",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(DatabeatController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class DatabeatController {

    static final String BASE_URL = "/v1/shard/databeat";

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatController.class);

    private final ShardService shardService;

    @Inject
    public DatabeatController(final ShardService shardService) {
        this.shardService = shardService;
    }

    @Get(uri = "generate")
    public HttpResponse<Boolean> generateDatabeat() {
        LOGGER.info("Databeat generation requested");
        shardService.generateDatabeat();
        return HttpResponse.ok(true);
    }

}
