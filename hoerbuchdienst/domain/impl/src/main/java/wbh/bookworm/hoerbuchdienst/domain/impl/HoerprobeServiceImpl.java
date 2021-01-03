package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            "abweichung",
            "bibliographisch",
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

    private final KatalogService katalogService;

    private final AudiobookStreamService audiobookStreamService;

    private final List<String> mp3Ignorieren = List.of(MP3_IGNORIEREN);

    @Inject
    public HoerprobeServiceImpl(final KatalogService katalogService,
                                final AudiobookStreamService audiobookStreamService) {
        this.katalogService = katalogService;
        this.audiobookStreamService = audiobookStreamService;
    }

    @Override
    public Optional<InputStream> makeHoerprobeAsStream(final String xMandant, final String xHoerernummer,
                                                       final String titelnummer) {
        final String ident = ermittleHoerprobe(xHoerernummer, titelnummer);
        if (null != ident && !ident.isBlank()) {
            LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörprobe '{}'", xHoerernummer, titelnummer, ident);
            try (final InputStream track = audiobookStreamService
                    .trackAsStream(xMandant, xHoerernummer, titelnummer, ident)) {
                LOGGER.info("Hörer '{}' Hörbuch '{}': Hörprobe '{}' erstellt", xHoerernummer, titelnummer, ident);
                return Optional.of(track);
            } catch (Exception e) {
                throw new HoerprobeServiceException("", e);
            }
        } else {
            LOGGER.error("Hörer '{}' Hörbuch '{}': Hörprobe kann nicht geliefert werden", xHoerernummer, titelnummer);
            return Optional.empty();
        }
    }

    private String ermittleHoerprobe(final String xHoerernummer, final String titelnummer) {
        final List<PlaylistEntryDTO> nachZeit = kandidatenNachZeit(titelnummer);
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Kandidat für eine Hörprobe nach Zeit: '{}'", xHoerernummer, titelnummer, nachZeit);
        final Map<Boolean, List<PlaylistEntryDTO>> nachName = kandidatenNachName(nachZeit);
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Kandiaten für eine Hörprobe nach Name: {}", nachName, xHoerernummer, titelnummer);
        String ident = zufaelligerIdent(nachName.get(false));
        if (ident.isBlank()) {
            final PlaylistDTO playlist = katalogService.playlist(titelnummer);
            final int index = Math.min(playlist.getEntries().size(), 5);
            if (playlist.getEntries().size() - 1 >= index) {
                ident = playlist.getEntries().get(index).getIdent();
            }
        }
        return ident;
    }

    private String zufaelligerIdent(final List<PlaylistEntryDTO> playlistEntries) {
        if (null != playlistEntries && !playlistEntries.isEmpty()) {
            int random = new Random().nextInt(playlistEntries.size() - 1);
            return playlistEntries.get(random).getIdent();
        } else {
            return "";
        }
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
                .filter(dto -> dto.getSeconds() > 10 && dto.getSeconds() < 30 * 60)
                .collect(Collectors.toUnmodifiableList());
    }

}
