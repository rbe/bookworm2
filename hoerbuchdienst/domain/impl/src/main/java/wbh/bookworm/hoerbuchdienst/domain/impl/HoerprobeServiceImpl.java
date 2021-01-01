package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookStreamService;
import wbh.bookworm.hoerbuchdienst.domain.ports.HoerprobeService;
import wbh.bookworm.hoerbuchdienst.domain.ports.HoerprobeServiceException;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistEntryDTO;

@Singleton
public final class HoerprobeServiceImpl implements HoerprobeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerprobeServiceImpl.class);

    private final KatalogService katalogService;

    private final AudiobookStreamService audiobookStreamService;

    private static final String[] MP3_IGNORIEREN = {
            "buch",
            "daisy",
            "urheberrecht",
            "eigentumsvermerk",
            "verfasser",
            "herausgeber",
            "produktion",
            "sprecher",
            "gesamtspieldauer",
            "gliederung",
            "abweichungen",
            "bibliographische",
            "spieldauer",
            "struktur",
            "klappentexte",
            "inhaltsverzeichnis",
            "glossar",
            "widmung",
            "nachwort",
            "literatur",
            "tips",
            "ende"
    };

    private final List<String> mp3Ignorieren = List.of(MP3_IGNORIEREN);

    @Inject
    public HoerprobeServiceImpl(final KatalogService katalogService,
                                final AudiobookStreamService audiobookStreamService) {
        this.katalogService = katalogService;
        this.audiobookStreamService = audiobookStreamService;
    }

    @Override
    public byte[] makeHoerprobeAsStream(final String xMandant, final String xHoerernummer,
                                        final String titelnummer) {
        final List<PlaylistEntryDTO> nachZeit = kandidatenNachZeit(titelnummer);
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Kandidat für eine Hörprobe nach Zeit: '{}'", xHoerernummer, titelnummer, nachZeit);
        final Map<Boolean, List<PlaylistEntryDTO>> nachName = kandidatenNachName(nachZeit);
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Kandiaten für eine Hörprobe nach Name: {}", nachName, xHoerernummer, titelnummer);
        if (!nachName.isEmpty()) {
            final String ident = zufaelligerIdent(nachName);
            LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörprobe '{}'", xHoerernummer, titelnummer, ident);
            try (final InputStream track = audiobookStreamService
                    .trackAsStream(xMandant, xHoerernummer, titelnummer, ident)) {
                LOGGER.info("Hörer '{}' Hörbuch '{}': Hörprobe '{}' erstellt", xHoerernummer, titelnummer, ident);
                return track.readAllBytes();
            } catch (Exception e) {
                throw new HoerprobeServiceException("", e);
            }
        } else {
            LOGGER.error("Hörer '{}' Hörbuch '{}': Hörprobe kann nicht geliefert werden", xHoerernummer, titelnummer);
            return new byte[0];
        }
    }

    private String zufaelligerIdent(final Map<Boolean, List<PlaylistEntryDTO>> playlistEntries) {
        final List<PlaylistEntryDTO> strings = playlistEntries.get(false);
        int random = new Random().nextInt(strings.size());
        return strings.get(random).getIdent();
    }

    private Map<Boolean, List<PlaylistEntryDTO>> kandidatenNachName(final List<PlaylistEntryDTO> playlistEntries) {
        return playlistEntries.stream()
                .collect(Collectors.partitioningBy(entry ->
                        mp3Ignorieren.stream().anyMatch(entry.getTitle()::contains)));
    }

    private List<PlaylistEntryDTO> kandidatenNachZeit(final String titelnummer) {
        final PlaylistDTO playlist = katalogService.playlist(titelnummer);
        return playlist.getEntries().stream()
                .sorted(Comparator.comparing(PlaylistEntryDTO::getSeconds))
                .filter(dto -> dto.getSeconds() > 10 && dto.getSeconds() < 15 * 60)
                .collect(Collectors.toUnmodifiableList());
    }

}
