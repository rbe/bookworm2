/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.util.List;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;

@Controller("/search")
public class SuchindexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuchindexController.class);

    private final KatalogService katalogService;

    @Inject
    public SuchindexController(final KatalogService katalogService) {
        this.katalogService = katalogService;
    }

    // TODO Automatisieren; hier nur für Testzwecke
    @Get(uri = "/index", produces = MediaType.APPLICATION_JSON)
    public boolean index() {
        return katalogService.index();
    }

    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public List<AudiobookInfoDTO> findAll(@Body final SuchindexAnfrageDTO suchindexAnfrageDTO) {
        return katalogService.findAll(suchindexAnfrageDTO.getHoerernummer(),
                suchindexAnfrageDTO.getKeywords());
    }

}
