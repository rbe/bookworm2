package wbh.bookworm.hoerbuchkatalog.webservice.rest;

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

import wbh.bookworm.hoerbuchkatalog.app.lieferung.CdLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.shared.domain.Hoerernummer;

@Tag(name = "Belastungen", description = "")
@RestController
@RequestMapping("/v1/hoererarchiv")
public class BelastungenRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BelastungenRestService.class);

    private final CdLieferungService cdLieferungService;

    private final HoerbuchResolver hoerbuchResolver;

    public BelastungenRestService(final CdLieferungService cdLieferungService,
                                  final HoerbuchResolver hoerbuchResolver) {
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @Operation(summary = "Belastungen abrufen und ggf. anhand Stichwort filtern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
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

}
