package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookStreamService;
import wbh.bookworm.hoerbuchdienst.domain.ports.HoerprobeService;
import wbh.bookworm.hoerbuchdienst.domain.ports.HoerprobeServiceException;
import wbh.bookworm.hoerbuchdienst.domain.ports.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.PlaylistEntryDTO;

import static java.util.function.Predicate.not;

@Singleton
public final class HoerprobeServiceImpl implements HoerprobeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerprobeServiceImpl.class);

    private static final String[] MP3_IGNORIEREN = {
            "",
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

    private static final Predicate<PlaylistEntryDTO> nachNamePredicate = entry -> {
        String str = "";
        if (null != entry.getTitle() && !entry.getTitle().isBlank()) {
            str = entry.getTitle();
        } else if (null != entry.getIdent() && !entry.getIdent().isBlank()) {
            str = entry.getIdent();
        }
        str = str.toLowerCase();
        return List.of(MP3_IGNORIEREN)
                .stream()
                .allMatch(not(str::contains));
    };

    private final Predicate<PlaylistEntryDTO> nachZeitPredicate = entry ->
            entry.getDuration().toSeconds() > 10L && entry.getDuration().toMinutes() < 10L;

    private final KatalogService katalogService;

    private final AudiobookStreamService audiobookStreamService;

    @Inject
    public HoerprobeServiceImpl(final KatalogService katalogService,
                                final AudiobookStreamService audiobookStreamService) {
        this.katalogService = katalogService;
        this.audiobookStreamService = audiobookStreamService;
    }

    @Override
    public Optional<InputStream> makeHoerprobeAsStream(final String xMandant, final String xHoerernummer,
                                                       final String titelnummer) {
        final Optional<String> maybeIdent = ermittleHoerprobe(xHoerernummer, titelnummer);
        if (maybeIdent.isPresent()) {
            final String ident = maybeIdent.get();
            LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörprobe '{}'", xHoerernummer, titelnummer, ident);
            try (final InputStream track = audiobookStreamService
                    .trackAsStream(xMandant, xHoerernummer, titelnummer, ident)) {
                LOGGER.info("Hörer '{}' Hörbuch '{}': Hörprobe '{}' erstellt", xHoerernummer, titelnummer, ident);
                return Optional.of(track);
            } catch (Exception e) {
                throw new HoerprobeServiceException(String.format("Hörbuch '%s'", titelnummer), e);
            }
        } else {
            LOGGER.error("Hörer '{}' Hörbuch '{}': Hörprobe kann nicht geliefert werden", xHoerernummer, titelnummer);
            return Optional.empty();
        }
    }

    private Optional<String> ermittleHoerprobe(final String xHoerernummer, final String titelnummer) {
        final PlaylistDTO playlist = katalogService.playlist(titelnummer);
        return zufaelligerIdent(kandidatenNachZeitUndName(playlist).get(true))
                .or(() -> zufaelligerIdent(kandidatenNachZeit(playlist).get(true)))
                .or(() -> zufaelligerIdent(kandidatenNachName(playlist).get(true)))
                .or(() -> {
                    final List<Path> mp3s = katalogService.playlistFuerHoerprobe(titelnummer);
                    LOGGER.debug("Hörer '{}' Hörbuch '{}': Keinen Kandidaten gefunden, wähle zufälligen Kandidaten aus Playlist: {}",
                            xHoerernummer, titelnummer, mp3s);
                    final int index = Math.min(mp3s.size(), new Random().nextInt(mp3s.size() - 1));
                    if (mp3s.size() - 1 >= index) {
                        return Optional.of(mp3s.get(index).getFileName().toString());
                    }
                    return Optional.empty();
                });
    }

    private Optional<String> zufaelligerIdent(final List<PlaylistEntryDTO> playlistEntries) {
        if (null != playlistEntries && playlistEntries.size() > 1) {
            int random = new Random().nextInt(playlistEntries.size() - 1);
            return Optional.of(playlistEntries.get(random).getIdent());
        } else if (null != playlistEntries && playlistEntries.size() == 1) {
            return Optional.of(playlistEntries.get(0).getIdent());
        } else {
            return Optional.empty();
        }
    }

    private Map<Boolean, List<PlaylistEntryDTO>> kandidatenNachZeitUndName(final PlaylistDTO playlist) {
        final Map<Boolean, List<PlaylistEntryDTO>> playlistEntries = playlist.getEntries().stream()
                .collect(Collectors.partitioningBy(entry ->
                        nachNamePredicate.test(entry) && nachZeitPredicate.test(entry)));
        LOGGER.debug("Hörbuch '{}': Kandidaten für eine Hörprobe nach Zeit und Name: '{}'", playlist.getTitelnummer(), playlistEntries);
        return playlistEntries;
    }

    private Map<Boolean, List<PlaylistEntryDTO>> kandidatenNachName(final PlaylistDTO playlist) {
        final Map<Boolean, List<PlaylistEntryDTO>> playlistEntries = playlist.getEntries().stream()
                .collect(Collectors.partitioningBy(nachNamePredicate));
        LOGGER.debug("Hörbuch '{}': Kandidaten für eine Hörprobe nach Name: '{}'", playlist.getTitelnummer(), playlistEntries);
        return playlistEntries;
    }

    private Map<Boolean, List<PlaylistEntryDTO>> kandidatenNachZeit(final PlaylistDTO playlist) {
        final Map<Boolean, List<PlaylistEntryDTO>> playlistEntries = playlist.getEntries().stream()
                .collect(Collectors.partitioningBy(nachZeitPredicate));
        LOGGER.debug("Hörbuch '{}': Kandidaten für eine Hörprobe nach Zeit: '{}'", playlist.getTitelnummer(), playlistEntries);
        return playlistEntries;
    }

}
