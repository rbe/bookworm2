/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
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
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.MandantService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistEntryDTO;
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
@Controller(value = StreamController.BASE_URL)
public class StreamController {

    static final String BASE_URL = "/v1/stream";

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamController.class);

    private static final String AUDIO_MP3 = "audio/mp3";

    private static final String APPLICATION_ZIP = "application/zip";
    //private static final MediaType APPLICATION_ZIP=MediaType.of("application/zip");

    private static final String EMPTY_STRING = "";

    private final AudiobookShardRedirector audiobookShardRedirector;

    private final AudiobookStreamService audiobookStreamService;

    private final MandantService mandantService;

    private final KatalogService katalogService;

    @Inject
    public StreamController(final AudiobookShardRedirector audiobookShardRedirector,
                            final AudiobookStreamService audiobookStreamService,
                            final MandantService mandantService,
                            final KatalogService katalogService) {
        this.audiobookShardRedirector = audiobookShardRedirector;
        this.audiobookStreamService = audiobookStreamService;
        this.mandantService = mandantService;
        this.katalogService = katalogService;
    }

    @Operation(hidden = true)
    @Options(uri = "{titelnummer}")
    public HttpResponse<String> optionsZippedAudiobookByTitelnummerAsStream(final HttpRequest<?> httpRequest,
                                                                            @PathVariable final String titelnummer) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Hörbuch (Titelnummer) als DAISY-ZIP")
    @ApiResponse(responseCode = "200", description = "DAISY-ZIP wird als Stream geliefert")
    @Post(uri = "{titelnummer}", consumes = MediaType.APPLICATION_JSON, produces = APPLICATION_ZIP)
    @Blocking
    public HttpResponse<byte[]> zippedAudiobookByTitelnummerAsStream(final HttpRequest<?> httpRequest,
                                                                     @Header("X-Bookworm-Mandant") final String xMandant,
                                                                     @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                     @PathVariable("titelnummer") final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeZippedAudiobook(xMandant, xHoerernummer, titelnummer),
                dto -> CORS.response(httpRequest, dto),
                String.format("%s/%s/zip", BASE_URL, titelnummer),
                httpRequest);
    }

    private byte[] makeZippedAudiobook(final String mandant, final String hoerernummer, final String titelnummer) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörbuch mit Wasserzeichen als ZIP",
                hoerernummer, titelnummer);
        final Path zip = audiobookStreamService.zipAsFile(mandant, hoerernummer, titelnummer);
        try {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Hörbuch mit Wasserzeichen als ZIP erstellt",
                    hoerernummer, titelnummer);
            final byte[] bytes = Files.readAllBytes(zip);
            Files.delete(zip);
            return bytes;
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

    @Operation(hidden = true)
    @Options(uri = "{titelnummer}/track/{ident}")
    public HttpResponse<String> optionsTrackAsStream(final HttpRequest<?> httpRequest,
                                                     @PathVariable final String titelnummer,
                                                     @PathVariable final String ident) {
        return optionsResponse(httpRequest);
    }

    @Operation(summary = "Track eines Hörbuchs als DAISY-ZIP")
    @Get(uri = "{titelnummer}/track/{ident}", consumes = MediaType.APPLICATION_JSON, produces = AUDIO_MP3)
    @Blocking
    public HttpResponse<byte[]> trackAsStream(final HttpRequest<?> httpRequest,
                                              @Header("X-Bookworm-Mandant") final String xMandant,
                                              @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                              @PathVariable("titelnummer") final String titelnummer,
                                              @PathVariable("ident") final String trackIdent) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeTrackAsStream(xMandant, xHoerernummer, titelnummer, trackIdent),
                body -> CORS.response(httpRequest, body)
                        .header("Accept-Ranges", "bytes"),
                String.format("%s/%s/track/%s", BASE_URL, titelnummer, trackIdent),
                httpRequest);
    }

    private byte[] makeTrackAsStream(final String xMandant, final String xHoerernummer,
                                     final String titelnummer, final String trackIdent) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Track '{}' mit Wasserzeichen",
                xHoerernummer, titelnummer, trackIdent);
        try (final InputStream track = audiobookStreamService.trackAsStream(xMandant,
                xHoerernummer, titelnummer, trackIdent)) {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Track '{}' mit Wasserzeichen erstellt",
                    xHoerernummer, titelnummer, trackIdent);
            return track.readAllBytes();
        } catch (Exception e) {
            throw new BusinessException(EMPTY_STRING, e);
        }
    }

    @Operation(summary = "Hörprobe eines Hörbuchs abrufen")
    @Get(uri = "{titelnummer}/hoerprobe", consumes = MediaType.APPLICATION_JSON, produces = AUDIO_MP3)
    @Blocking
    public HttpResponse<byte[]> hoerprobeAsStream(final HttpRequest<?> httpRequest,
                                                  @Header("X-Bookworm-Mandant") final String xMandant,
                                                  @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                  @PathVariable("titelnummer") final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeHoerprobeAsStream(xMandant, xHoerernummer, titelnummer, 5),
                body -> CORS.response(httpRequest, body)
                        .header("Accept-Ranges", "bytes"),
                String.format("%s/track", BASE_URL),
                httpRequest);
    }

    private byte[] makeHoerprobeAsStream(final String xMandant, final String xHoerernummer,
                                         final String titelnummer, final int trackIndex) {
        final PlaylistDTO playlist = katalogService.playlist(titelnummer);
        final boolean hasEnoughTracks = playlist.getEntries().size() >= trackIndex;
        final PlaylistEntryDTO playlistEntryDTO;
        if (hasEnoughTracks) {
            playlistEntryDTO = playlist.getEntries().get(trackIndex);
            LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörprobe '{}' mit Wasserzeichen",
                    xHoerernummer, titelnummer, playlistEntryDTO.getIdent());
            try (final InputStream track = audiobookStreamService.trackAsStream(xMandant,
                    xHoerernummer, titelnummer, playlistEntryDTO.getIdent())) {
                LOGGER.info("Hörer '{}' Hörbuch '{}': Hörprobe '{}' mit Wasserzeichen erstellt",
                        xHoerernummer, titelnummer, playlistEntryDTO.getIdent());
                return track.readAllBytes();
            } catch (Exception e) {
                throw new BusinessException(EMPTY_STRING, e);
            }
        } else {
            LOGGER.error("Hörer '{}' Hörbuch '{}': Hörprobe kann nicht geliefert werden",
                    xHoerernummer, titelnummer);
            return new byte[0];
        }
    }

}
