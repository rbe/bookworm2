/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Path;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookStreamService;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

import static wbh.bookworm.hoerbuchdienst.sharding.shared.CORS.optionsResponse;

@OpenAPIDefinition(
        info = @Info(
                title = "Bestellung",
                version = "1.0.0",
                description = "Hoerbuchdienst - Hörbücher bestellen",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(value = HoerbuchController.BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HoerbuchController {

    static final String BASE_URL = "/v1/hoerbuch";

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchController.class);

    private static final String APPLICATION_ZIP_VALUE = "application/zip";

    private static final MediaType APPLICATION_ZIP = MediaType.of("application/zip");

    private static final String EMPTY_STRING = "";

    private final AudiobookShardRedirector audiobookShardRedirector;

    private final AudiobookStreamService audiobookStreamService;

    @Inject
    public HoerbuchController(final AudiobookShardRedirector audiobookShardRedirector,
                              final AudiobookStreamService audiobookStreamService) {
        this.audiobookShardRedirector = audiobookShardRedirector;
        this.audiobookStreamService = audiobookStreamService;
    }

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}")
    public HttpResponse<String> optionsZippedAudiobookByTitelnummerAsStream(final HttpRequest<?> httpRequest,
                                                                            @PathVariable final String titelnummer) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Hörbuch (Titelnummer) als DAISY-ZIP")
    @ApiResponse(responseCode = "200", description = "DAISY-ZIP wird als Stream geliefert")
    @Get(uri = "/{titelnummer}")
    @Produces(APPLICATION_ZIP_VALUE)
    @Blocking
    public HttpResponse<SystemFile> daisyZipAsFile(final HttpRequest<?> httpRequest,
                                                   @Header("X-Bookworm-Mandant") final String xMandant,
                                                   @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                   @PathVariable("titelnummer") final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeDaisyZipFile(xMandant, xHoerernummer, titelnummer),
                dto -> CORS.response(httpRequest, dto)
                        .contentType(APPLICATION_ZIP_VALUE)
                        .contentLength(dto.getLength())
                        .header("Content-Disposition", String.format("attachment; filename=\"%s.zip\"", titelnummer)),
                String.format("%s/%s", BASE_URL, titelnummer),
                httpRequest);
    }

    private SystemFile makeDaisyZipFile(final String mandant, final String hoerernummer, final String titelnummer) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle DAISY Hörbuch als ZIP-Datei",
                hoerernummer, titelnummer);
        try {
            final long start = System.nanoTime();
            final Path zip = audiobookStreamService.zipAsFile(mandant, hoerernummer, titelnummer);
            final long stop = System.nanoTime();
            LOGGER.info("Hörer '{}' Hörbuch '{}': DAISY Hörbuch als ZIP-Datei in {} ms = {} s erstellt",
                    hoerernummer, titelnummer, (stop - start) / 1_000_000L, (stop - start) / 1_000_000L / 1_000L);
            return new SystemFile(zip.toFile()).attach(String.format("%s.zip", titelnummer));
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

    @Operation(summary = "Hörbuch (Titelnummer) als DAISY-ZIP")
    @ApiResponse(responseCode = "200", description = "DAISY-ZIP wird als Stream geliefert")
    @Get(uri = "/{titelnummer}/stream")
    @Produces(APPLICATION_ZIP_VALUE)
    @Blocking
    public HttpResponse<StreamedFile> daisyZipAsStream(final HttpRequest<?> httpRequest,
                                                       @Header("X-Bookworm-Mandant") final String xMandant,
                                                       @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                       @PathVariable("titelnummer") final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeDaisyZipStream(xMandant, xHoerernummer, titelnummer),
                dto -> CORS.response(httpRequest, dto)
                        .contentType(APPLICATION_ZIP_VALUE)
                        .contentLength(dto.getLength())
                        .header("Content-Disposition", String.format("attachment; filename=\"%s.zip\"", titelnummer)),
                String.format("%s/%s", BASE_URL, titelnummer),
                httpRequest);
    }

    private StreamedFile makeDaisyZipStream(final String mandant, final String hoerernummer, final String titelnummer) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle DAISY Hörbuch als ZIP-Datei", hoerernummer, titelnummer);
        final long start = System.nanoTime();
        try (final InputStream zip = audiobookStreamService.zipAsStream(mandant, hoerernummer, titelnummer)) {
            final long stop = System.nanoTime();
            LOGGER.info("Hörer '{}' Hörbuch '{}': DAISY Hörbuch als ZIP-Datei in {} ms = {} s erstellt",
                    hoerernummer, titelnummer, (stop - start) / 1_000_000L, (stop - start) / 1_000_000L / 1_000L);
            return new StreamedFile(zip, APPLICATION_ZIP)
                    .attach(String.format("%s.zip", titelnummer));
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

}
