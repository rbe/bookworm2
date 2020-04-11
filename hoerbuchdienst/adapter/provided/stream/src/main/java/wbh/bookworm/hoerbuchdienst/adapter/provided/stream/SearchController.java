/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.util.List;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.CatalogService;

@OpenAPIDefinition(
        info = @Info(title = "wbh.sds", version = "0.0")
)
@Controller("/search")
@Slf4j
public class SearchController {

    private final CatalogService catalogService;

    @Inject
    public SearchController(final CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Get(uri = "/index", produces = MediaType.APPLICATION_JSON)
    public boolean index() {
        return catalogService.index();
    }

    @Get(uri = "/{keyword}", produces = MediaType.APPLICATION_JSON)
    public List<AudiobookInfoDTO> findAll(@PathVariable final String keyword) {
        return catalogService.findAll(keyword);
    }

}
