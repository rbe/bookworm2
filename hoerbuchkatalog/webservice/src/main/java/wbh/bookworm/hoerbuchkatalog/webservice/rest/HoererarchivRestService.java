package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;

import org.springframework.http.MediaType;
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

    private final TitelnummerResolver titelnummerResolver;

    public HoererarchivRestService(final CdLieferungService cdLieferungService,
                                   final TitelnummerResolver titelnummerResolver) {
        this.cdLieferungService = cdLieferungService;
        this.titelnummerResolver = titelnummerResolver;
    }

    @GetMapping(value = "/belastungen", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BelastungAntwortDTO> belastungen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                 @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final List<Belastung> belastungen = cdLieferungService.belastungen(new Hoerernummer(xHoerernummer));
        return titelnummerResolver.toBelastungenAntwortDTO(belastungen);
    }

    @GetMapping(value = "/bestellkarten", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BestellkarteAntwortDTO> bestellkarten(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                      @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final List<Bestellkarte> bestellkarten = cdLieferungService.bestellkarten(new Hoerernummer(xHoerernummer));
        return titelnummerResolver.toBestellkarteAntwortDTO(bestellkarten);
    }

    @GetMapping(value = "/erledigteBestellkarten", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ErledigteBestellkarteAntwortDTO> erledigteBestellkarten(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                        @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final List<ErledigteBestellkarte> erledigteBestellkarten = cdLieferungService.erledigteBestellkarten(new Hoerernummer(xHoerernummer));
        return titelnummerResolver.toErledigteBestellkarteAntwortDTO(erledigteBestellkarten);
    }

}
