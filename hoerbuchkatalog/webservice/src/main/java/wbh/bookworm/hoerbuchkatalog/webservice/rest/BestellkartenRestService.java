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
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.shared.domain.Hoerernummer;

@Tag(name = "Bestellkarten", description = "")
@RestController
@RequestMapping("/v1/hoererarchiv")
public class BestellkartenRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellkartenRestService.class);

    private final CdLieferungService cdLieferungService;

    private final HoerbuchResolver hoerbuchResolver;

    public BestellkartenRestService(final CdLieferungService cdLieferungService,
                                    final HoerbuchResolver hoerbuchResolver) {
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @Operation(summary = "Bestellkarten abrufen und ggf. anhand Stichwort filtern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
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

}
