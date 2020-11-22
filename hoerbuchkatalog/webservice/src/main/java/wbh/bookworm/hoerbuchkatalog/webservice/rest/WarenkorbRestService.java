package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/warenkorb")
public class WarenkorbRestService {

    private final WarenkorbService warenkorbService;

    private final BestellungService bestellungService;

    private final HoerbuchResolver hoerbuchResolver;

    public WarenkorbRestService(final WarenkorbService warenkorbService,
                                final BestellungService bestellungService,
                                final HoerbuchResolver hoerbuchResolver) {
        this.warenkorbService = warenkorbService;
        this.bestellungService = bestellungService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                              @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                              @RequestBody final HoerbuchAnfrageDTO hoerbuchAnfrageDTO) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(
                xHoerernummer);
        return warenkorbService.inDenCdWarenkorb(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                new Titelnummer(hoerbuchAnfrageDTO.getTitelnummer()));
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                             @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                             @RequestBody final HoerbuchAnfrageDTO hoerbuchAnfrageDTO) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(
                xHoerernummer);
        warenkorbService.ausDemCdWarenkorbEntfernen(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                new Titelnummer(hoerbuchAnfrageDTO.getTitelnummer()));
        return true;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortDTO> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final CdWarenkorb cdWarenkorb = warenkorbService.cdWarenkorbKopie(
                bestellungService.bestellungSessionId(xHoerernummer),
                new Hoerernummer(xHoerernummer));
        return hoerbuchResolver.toHoerbuchAntwortDTO(new ArrayList<>(cdWarenkorb.getTitelnummern()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String bestellen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                            @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                            @RequestBody final BestellungAnfrageDTO bestellungAnfrageDTO) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(xHoerernummer);
        return bestellungService.bestellungAufgeben(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                Hoerername.of(bestellungAnfrageDTO.getHoerername()),
                new HoererEmail(bestellungAnfrageDTO.getHoereremail()),
                bestellungAnfrageDTO.getBemerkung(),
                bestellungAnfrageDTO.getBestellkarteMischen(),
                bestellungAnfrageDTO.getAlteBestellkarteLoeschen())
                .orElseThrow()
                .getValue();
    }

}
