package wbh.bookworm.hoerbuchkatalog.webservice.erledigtebestellkarte;

import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.CdLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.webservice.api.Antwort;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Tag(name = "Erledigte Bestellkarten", description = "Erledigte Bestellkarten abfragen und durchsuchen")
@RestController
@RequestMapping("/v1/hoererarchiv")
public class ErledigteBestellkartenRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErledigteBestellkartenRestService.class);

    private static final Hoerernummer HOERERNUMMER = new Hoerernummer("00000");

    private final CdLieferungService cdLieferungService;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    public ErledigteBestellkartenRestService(final CdLieferungService cdLieferungService, final HoerbuchkatalogService hoerbuchkatalogService) {
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    @Operation(summary = "Erledigte Bestellkarten abrufen und ggf. anhand Stichwort und Startdatum filtern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(value = {
            "/erledigteBestellkarten",
            "/erledigteBestellkarten/stichwort/{stichwort}",
            "/erledigteBestellkarten/startdatum/{startdatum}",
            "/erledigteBestellkarten/stichwort/{stichwort}/startdatum/{startdatum}",
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<List<ErledigteBestellkarte>>> erledigteBestellkarten(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                                       @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                                       @PathVariable(required = false) final String stichwort,
                                                                                       @PathVariable(required = false) final String startdatum) {
        final List<wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte> erledigteBestellkarten = cdLieferungService.erledigteBestellkarten(new Hoerernummer(xHoerernummer));
        if (erledigteBestellkarten.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<ErledigteBestellkarte> data = toErledigteBestellkarteAntwortDTO(erledigteBestellkarten);
        List<ErledigteBestellkarte> filtered;
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
        return ResponseEntity.ok(new Antwort<>(meta, filtered));
    }

    @SuppressWarnings("squid:S3864")
    private List<ErledigteBestellkarte> toErledigteBestellkarteAntwortDTO(final List<wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte> erledigteBestellkarten) {
        final List<ErledigteBestellkarte> erledigteBestellkartes = ErledigteBestellkarteMapper.INSTANCE.convert(erledigteBestellkarten);
        return erledigteBestellkartes.stream()
                .peek(antwortDTO -> {
                    final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(HOERERNUMMER, new Titelnummer(antwortDTO.getTitelnummer()));
                    antwortDTO.setAutor(hoerbuch.getAutor());
                    antwortDTO.setTitel(hoerbuch.getTitel());
                    antwortDTO.setSachgebiet(hoerbuch.getSachgebiet().getName());
                    antwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
                    antwortDTO.setSprecher1(hoerbuch.getSprecher1());
                    antwortDTO.setSprecher2(hoerbuch.getSprecher2());
                    antwortDTO.setSpieldauer(hoerbuch.getSpieldauer());
                })
                .collect(Collectors.toList());
    }

}
