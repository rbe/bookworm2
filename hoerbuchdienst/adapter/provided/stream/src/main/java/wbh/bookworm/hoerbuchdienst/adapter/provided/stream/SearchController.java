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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;

@OpenAPIDefinition(
        info = @Info(title = "wbh.sds", version = "0.0")
)
@Controller("/search")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private final KatalogService katalogService;

    @Inject
    public SearchController(final KatalogService katalogService) {
        this.katalogService = katalogService;
    }

    // TODO Automatisieren; hier nur für Testzwecke
    @Get(uri = "/index", produces = MediaType.APPLICATION_JSON)
    public boolean index() {
        return katalogService.index();
    }

    @Get(uri = "/{hoerernummer}/{keyword}", produces = MediaType.APPLICATION_JSON)
    public List<AudiobookInfoDTO> findAll(@PathVariable final String hoerernummer,
                                          @PathVariable final String keyword) {
        return katalogService.findAll(keyword);
    }

}
