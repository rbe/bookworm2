package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/private/downloads")
public class WbhDownloadsStatisticsRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WbhDownloadsStatisticsRestService.class);

    private static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter ZEIT = DateTimeFormatter.ofPattern("HHmmss");

    private final DownloadsService downloadsService;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    @Autowired
    public WbhDownloadsStatisticsRestService(final DownloadsService downloadsService,
                                             final HoerbuchkatalogService hoerbuchkatalogService) {
        this.downloadsService = downloadsService;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    @GetMapping(value = "webhoer-{datum}.csv", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> downloadsHeuteAbrufen(@PathVariable final String datum) {
        final LocalDateTime datumZeit = LocalDate.parse(datum, DATUM).atStartOfDay();
        final byte[] body = erstelleCsvDatei(LocalDateTime.now(), datumZeit);
        LOGGER.info("webhoer-{}.csv erzeugt, {} bytes = {} kB = {} MB", datum,
                body.length, body.length / 1_024L, body.length / 1_024L / 1_024L);
        return ResponseEntity.ok(body);
    }

    private byte[] erstelleCsvDatei(final LocalDateTime now, final LocalDateTime datum) {
        return downloadsService.alle()
                .stream()
                .flatMap(Collection::stream)
                .map(download -> getCollect(now, datum, download))
                .flatMap(Collection::stream)
                .collect(Collectors.joining("\r\n"))
                .getBytes(StandardCharsets.ISO_8859_1);
    }

    private List<String> getCollect(final LocalDateTime now, final LocalDateTime datum,
                                    final Downloads download) {
        return download.getTitelnummern()
                .entrySet()
                .stream()
                .filter(e -> isSameDay(datum, e))
                .map(e -> csvEintrag(now, download, e))
                .collect(Collectors.toUnmodifiableList());
    }

    private String csvEintrag(final LocalDateTime now,
                              final Downloads download,
                              final Map.Entry<Titelnummer, Downloads.Details> e) {
        final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(download.getHoerernummer(),
                e.getKey());
        return String.format("%5s %6s %13s %11s %8s %6s %1s %8s %6s %8s %6s",
                /* HOENR */download.getHoerernummer(),
                /* TITNR */e.getKey(),
                /* TIAGNR */hoerbuch.getAghNummer(),
                /* DLSID */"unbekannt01",
                /* ABFRDT */now.format(DATUM),
                /* ABFRZT */now.format(ZEIT),
                /* STATUS */"0",
                /* AUSLDT, Ausleihdatum */now.format(DATUM),
                /* AUSLZT, Ausleihdatum */now.format(ZEIT),
                /* RUEGDT */e.getValue().getRueckgabeBis().format(DATUM),
                /* RUEGZT */e.getValue().getRueckgabeBis().format(ZEIT));
    }

    private boolean isSameDay(final LocalDateTime datum, final Map.Entry<Titelnummer, Downloads.Details> e) {
        final LocalDateTime ausgeliehenAm = e.getValue().getAusgeliehenAm();
        return ausgeliehenAm.getYear() == datum.getYear()
                && ausgeliehenAm.getMonth() == datum.getMonth()
                && ausgeliehenAm.getDayOfMonth() == datum.getDayOfMonth();
    }

}
