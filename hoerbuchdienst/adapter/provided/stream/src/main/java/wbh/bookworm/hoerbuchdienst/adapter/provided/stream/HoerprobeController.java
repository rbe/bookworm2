/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookStreamService;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistEntryDTO;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

@OpenAPIDefinition(
        info = @Info(
                title = "Bestellung",
                version = "1.0.0",
                description = "Hoerbuchdienst - Hörbücher bestellen",
                license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
                contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
        )
)
@Controller(value = HoerprobeController.BASE_URL)
public class HoerprobeController {

    static final String BASE_URL = "/v1/hoerprobe";

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerprobeController.class);

    private static final String AUDIO_MP3 = "audio/mp3";

    private static final String EMPTY_STRING = "";

    private final AudiobookShardRedirector audiobookShardRedirector;

    private final AudiobookStreamService audiobookStreamService;

    private final KatalogService katalogService;

    @Inject
    public HoerprobeController(final AudiobookShardRedirector audiobookShardRedirector,
                               final AudiobookStreamService audiobookStreamService,
                               final KatalogService katalogService) {
        this.audiobookShardRedirector = audiobookShardRedirector;
        this.audiobookStreamService = audiobookStreamService;
        this.katalogService = katalogService;
    }

    @Operation(summary = "Hörprobe eines Hörbuchs abrufen")
    @Get(uri = "/{titelnummer}", consumes = MediaType.APPLICATION_JSON, produces = AUDIO_MP3)
    @Blocking
    public HttpResponse<byte[]> hoerprobeAsStream(final HttpRequest<?> httpRequest,
                                                  @Header("X-Bookworm-Mandant") final String xMandant,
                                                  @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                  @PathVariable("titelnummer") final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeHoerprobeAsStream(xMandant, xHoerernummer, titelnummer, 5),
                body -> CORS.response(httpRequest, body)
                        .header("Accept-Ranges", "bytes"),
                String.format("%s/%s/hoerprobe", BASE_URL, titelnummer),
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
