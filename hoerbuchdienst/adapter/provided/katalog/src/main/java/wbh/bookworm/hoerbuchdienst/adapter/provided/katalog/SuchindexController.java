/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.katalog;

import javax.inject.Inject;
import java.util.List;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;

@OpenAPIDefinition(
        info = @Info(
                title = "Katalog",
                version = "1.0.0",
                description = "Hoerbuchdienst - Katalog",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(SuchindexController.BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SuchindexController {

    static final String BASE_URL = "/v1/suche";

    private static final Logger LOGGER = LoggerFactory.getLogger(SuchindexController.class);

    private final KatalogService katalogService;

    @Inject
    public SuchindexController(final KatalogService katalogService) {
        this.katalogService = katalogService;
    }

    // TODO Automatisieren; hier nur für Testzwecke
    @Get(uri = "index")
    public boolean index() {
        return katalogService.index();
    }

    @Post
    public List<SuchindexAntwortDTO> findAll(@Body final SuchindexAnfrageDTO suchindexAnfrageDTO) {
        final List<AudiobookInfoDTO> result = katalogService.findAll(suchindexAnfrageDTO.getKeywords());
        return AntwortMapper.INSTANCE.convert(result);
    }

    @Mapper
    public interface AntwortMapper {

        AntwortMapper INSTANCE = Mappers.getMapper(AntwortMapper.class);

        List<SuchindexAntwortDTO> convert(List<AudiobookInfoDTO> audiobookInfoDTO);

    }

}
