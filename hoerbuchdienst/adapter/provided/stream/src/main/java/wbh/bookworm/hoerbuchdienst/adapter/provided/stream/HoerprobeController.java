/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(HoerprobeController.AUDIO_MP3)
public class HoerprobeController {

    static final String BASE_URL = "/v1/hoerprobe";

    static final String AUDIO_MP3 = "audio/mp3";

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerprobeController.class);

    private static final String EMPTY_STRING = "";

    private static final String[] MP3_IGNORIEREN = {
            "Buch",
            "DAISY",
            "Urheberrecht",
            "Eigentumsvermerk",
            "Verfasser",
            "Herausgeber",
            "Produktion",
            "Sprecher",
            "Gesamtspieldauer",
            "Gliederung",
            "Abweichungen",
            "Buchaufsprache",
            "Bibliographische",
            "Spieldauer",
            "Struktur",
            "Klappentexte",
            "Buchinnenseite",
            "Inhaltsverzeichnis",
            "Glossar",
            "Widmung",
            "Nachwort",
            "Buchzus",
            "Literatur",
            "Tips",
            "Ende"
    };

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

    @Operation(hidden = true)
    @Options(uri = "/{titelnummer}")
    public MutableHttpResponse<String> optionsHoerprobeAsStream(final HttpRequest<?> httpRequest,
                                                                @PathVariable("titelnummer") final String titelnummer) {
        return CORS.optionsResponse(httpRequest);
    }

    @Operation(summary = "Hörprobe eines Hörbuchs abrufen")
    @Get(uri = "/{titelnummer}")
    @Blocking
    public HttpResponse<byte[]> hoerprobeAsStream(final HttpRequest<?> httpRequest,
                                                  @Header("X-Bookworm-Mandant") final String xMandant,
                                                  @Header("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                  @PathVariable("titelnummer") final String titelnummer) {
        return audiobookShardRedirector.withLocalOrRedirect(titelnummer,
                () -> makeHoerprobeAsStream(xMandant, xHoerernummer, titelnummer),
                body -> CORS.response(httpRequest, body)
                        .header("Accept-Ranges", "bytes"),
                String.format("%s/%s", BASE_URL, titelnummer),
                httpRequest);
    }

    private byte[] makeHoerprobeAsStream(final String xMandant, final String xHoerernummer,
                                         final String titelnummer) {
        final PlaylistDTO playlist = katalogService.playlist(titelnummer);
        final List<String> mp3s = playlist.getEntries().stream()
                .map(PlaylistEntryDTO::getIdent)
                .filter(ident -> ident.toLowerCase().endsWith("mp3"))
                .collect(Collectors.toUnmodifiableList());
        final Map<Boolean, List<String>> filteredMp3s = mp3s.stream()
                .collect(Collectors.partitioningBy(List.of(MP3_IGNORIEREN)::contains));
        LOGGER.debug("Kandiaten für eine Hörprobe: {}, Gesamte MP3s {}", filteredMp3s, mp3s);
        if (!filteredMp3s.isEmpty()) {
            final List<String> strings = filteredMp3s.get(false);
            int random = new Random().nextInt(strings.size());
            final String ident = strings.get(random);
            LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörprobe '{}' mit Wasserzeichen",
                    xHoerernummer, titelnummer, ident);
            try (final InputStream track = audiobookStreamService.trackAsStream(xMandant,
                    xHoerernummer, titelnummer, ident)) {
                LOGGER.info("Hörer '{}' Hörbuch '{}': Hörprobe '{}' mit Wasserzeichen erstellt",
                        xHoerernummer, titelnummer, ident);
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
