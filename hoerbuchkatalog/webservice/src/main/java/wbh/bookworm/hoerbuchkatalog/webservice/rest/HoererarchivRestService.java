package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final CdLieferungService cdLieferungService;

    private final HoerbuchResolver hoerbuchResolver;

    public HoererarchivRestService(final CdLieferungService cdLieferungService,
                                   final HoerbuchResolver hoerbuchResolver) {
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @GetMapping(value = "/belastungen", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<BelastungAntwortDTO>>> belastungen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                             @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final List<Belastung> belastungen = cdLieferungService.belastungen(new Hoerernummer(xHoerernummer));
        if (belastungen.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<BelastungAntwortDTO> data = hoerbuchResolver.toBelastungenAntwortDTO(belastungen);
        return ResponseEntity.ok(new AntwortDTO<>(Map.of("count", data.size()), data));
    }

    @GetMapping(value = "/bestellkarten", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<BestellkarteAntwortDTO>>> bestellkarten(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                                  @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final List<Bestellkarte> bestellkarten = cdLieferungService.bestellkarten(new Hoerernummer(xHoerernummer));
        if (bestellkarten.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<BestellkarteAntwortDTO> data = hoerbuchResolver.toBestellkarteAntwortDTO(bestellkarten);
        return ResponseEntity.ok(new AntwortDTO<>(Map.of("count", data.size()), data));
    }

    @GetMapping(value = "/erledigteBestellkarten", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<ErledigteBestellkarteAntwortDTO>>> erledigteBestellkarten(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                                                    @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final List<ErledigteBestellkarte> erledigteBestellkarten = cdLieferungService.erledigteBestellkarten(new Hoerernummer(xHoerernummer));
        if (erledigteBestellkarten.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final List<ErledigteBestellkarteAntwortDTO> data = hoerbuchResolver.toErledigteBestellkarteAntwortDTO(erledigteBestellkarten);
        return ResponseEntity.ok(new AntwortDTO<>(Map.of("count", data.size()), data));
    }

}
