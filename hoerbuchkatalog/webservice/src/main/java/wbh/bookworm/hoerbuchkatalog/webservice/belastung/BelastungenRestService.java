package wbh.bookworm.hoerbuchkatalog.webservice.belastung;

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

@Tag(name = "Belastungen", description = "")
@RestController
@RequestMapping("/v1/hoererarchiv")
public class BelastungenRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BelastungenRestService.class);

    private static final Hoerernummer HOERERNUMMER = new Hoerernummer("00000");

    private final CdLieferungService cdLieferungService;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    public BelastungenRestService(final CdLieferungService cdLieferungService,
                                  final HoerbuchkatalogService hoerbuchkatalogService) {
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    @Operation(summary = "Belastungen abrufen und ggf. anhand Stichwort filtern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(value = {
            "/belastungen",
            "/belastungen/stichwort/{stichwort}"
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<List<Belastung>>> belastungen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                @PathVariable(required = false) final String stichwort) {
        final List<wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung> belastungen = cdLieferungService.belastungen(new Hoerernummer(xHoerernummer));
        if (belastungen.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<Belastung> data = toBelastungenAntwortDTO(belastungen);
        final List<Belastung> filtered;
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
        return ResponseEntity.ok(new Antwort<>(meta, filtered));
    }

    @SuppressWarnings("squid:S3864")
    private List<Belastung> toBelastungenAntwortDTO(final List<wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung> belastungen) {
        final List<Belastung> belastungs = BelastungMapper.INSTANCE.convert(belastungen);
        return belastungs.stream()
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
