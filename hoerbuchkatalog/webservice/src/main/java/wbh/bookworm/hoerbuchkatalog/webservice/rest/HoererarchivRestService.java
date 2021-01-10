package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.lieferung.CdLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.shared.domain.Hoerernummer;

@RestController
@RequestMapping("/v1/hoererarchiv")
public class HoererarchivRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererarchivRestService.class);

    private final CdLieferungService cdLieferungService;

    private final HoerbuchResolver hoerbuchResolver;

    public HoererarchivRestService(final CdLieferungService cdLieferungService,
                                   final HoerbuchResolver hoerbuchResolver) {
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @GetMapping(value = {
            "/belastungen",
            "/belastungen/stichwort/{stichwort}"
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<BelastungAntwortDTO>>> belastungen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                             @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                             @PathVariable(required = false) final String stichwort) {
        final List<Belastung> belastungen = cdLieferungService.belastungen(new Hoerernummer(xHoerernummer));
        if (belastungen.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<BelastungAntwortDTO> data = hoerbuchResolver.toBelastungenAntwortDTO(belastungen);
        final List<BelastungAntwortDTO> filtered;
        if (null != stichwort && !stichwort.isBlank() && !stichwort.equals("*")) {
            final String s = stichwort.toLowerCase();
            filtered = data.stream()
                    .filter(dto -> dto.getAutor().toLowerCase().contains(s)
                            || dto.getSachgebietBezeichnung().toLowerCase().contains(s)
                            || dto.getTitel().toLowerCase().contains(s)
                            || dto.getSprecher1().toLowerCase().contains(s)
                            || dto.getSprecher2().toLowerCase().contains(s))
                    .collect(Collectors.toUnmodifiableList());
        } else {
            filtered = data;
        }
        final Map<String, Object> meta = Map.of("count", filtered.size(),
                "stichwort", null != stichwort ? stichwort : "");
        return ResponseEntity.ok(new AntwortDTO<>(meta, filtered));
    }

    @GetMapping(value = {
            "/bestellkarten",
            "/bestellkarten/stichwort/{stichwort}"
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<BestellkarteAntwortDTO>>> bestellkarten(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                                  @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                                  @PathVariable(required = false) final String stichwort) {
        final List<Bestellkarte> bestellkarten = cdLieferungService.bestellkarten(new Hoerernummer(xHoerernummer));
        if (bestellkarten.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<BestellkarteAntwortDTO> data = hoerbuchResolver.toBestellkarteAntwortDTO(bestellkarten);
        final List<BestellkarteAntwortDTO> filtered;
        if (null != stichwort && !stichwort.isBlank() && !stichwort.equals("*")) {
            final String s = stichwort.toLowerCase();
            filtered = data.stream()
                    .filter(dto -> dto.getAutor().toLowerCase().contains(s)
                            || dto.getSachgebietBezeichnung().toLowerCase().contains(s)
                            || dto.getTitel().toLowerCase().contains(s)
                            || dto.getSprecher1().toLowerCase().contains(s)
                            || dto.getSprecher2().toLowerCase().contains(s))
                    .collect(Collectors.toUnmodifiableList());
        } else {
            filtered = data;
        }
        final Map<String, Object> meta = Map.of("count", filtered.size(),
                "stichwort", null != stichwort ? stichwort : "");
        return ResponseEntity.ok(new AntwortDTO<>(meta, filtered));
    }

    @GetMapping(value = {
            "/erledigteBestellkarten",
            "/erledigteBestellkarten/stichwort/{stichwort}",
            "/erledigteBestellkarten/startdatum/{startdatum}",
            "/erledigteBestellkarten/stichwort/{stichwort}/startdatum/{startdatum}",
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<ErledigteBestellkarteAntwortDTO>>> erledigteBestellkarten(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                                                    @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                                                    @PathVariable(required = false) final String stichwort,
                                                                                                    @PathVariable(required = false) final String startdatum) {
        final List<ErledigteBestellkarte> erledigteBestellkarten = cdLieferungService.erledigteBestellkarten(new Hoerernummer(xHoerernummer));
        if (erledigteBestellkarten.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<ErledigteBestellkarteAntwortDTO> data = hoerbuchResolver.toErledigteBestellkarteAntwortDTO(erledigteBestellkarten);
        List<ErledigteBestellkarteAntwortDTO> filtered;
        if (null != stichwort && !stichwort.isBlank() && !stichwort.equals("*")) {
            final String s = stichwort.toLowerCase();
            filtered = data.stream()
                    .filter(dto -> dto.getAutor().toLowerCase().contains(s)
                            || dto.getSachgebietBezeichnung().toLowerCase().contains(s)
                            || dto.getTitel().toLowerCase().contains(s)
                            || dto.getSprecher1().toLowerCase().contains(s)
                            || dto.getSprecher2().toLowerCase().contains(s))
                    .collect(Collectors.toUnmodifiableList());
        } else {
            filtered = data;
        }
        if (null != startdatum && !startdatum.isBlank() && !startdatum.equals("*")) {
            try {
                final TemporalAccessor datum = DateTimeFormatter.ofPattern("dd.MM.yyyy").parse(startdatum);
                filtered = filtered.stream()
                        .filter(dto -> dto.getAusleihdatum().isAfter(ChronoLocalDate.from(datum)))
                        .collect(Collectors.toUnmodifiableList());
            } catch (DateTimeParseException e) {
                LOGGER.warn("Kann Startdatum '{}' nicht parsen", startdatum);
            }
        }
        final Map<String, Object> meta = Map.of("count", filtered.size(),
                "stichwort", null != stichwort ? stichwort : "",
                "startdatum", null != startdatum ? startdatum : "");
        return ResponseEntity.ok(new AntwortDTO<>(meta, filtered));
    }

}
