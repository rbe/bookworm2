package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/warenkorb")
public final class WarenkorbRestService {

    private final WarenkorbService warenkorbService;

    private final BestellungService bestellungService;

    private final MerklisteService merklisteService;

    private final HoerbuchResolver hoerbuchResolver;

    @Autowired
    public WarenkorbRestService(final WarenkorbService warenkorbService,
                                final BestellungService bestellungService,
                                final MerklisteService merklisteService,
                                final HoerbuchResolver hoerbuchResolver) {
        this.warenkorbService = warenkorbService;
        this.bestellungService = bestellungService;
        this.merklisteService = merklisteService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    //@PutMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                          @PathVariable final String titelnummer) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(
                xHoerernummer);
        final boolean b = warenkorbService.inDenCdWarenkorb(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return Map.of("result", b);
    }

    @DeleteMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                         @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                         @PathVariable final String titelnummer) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(
                xHoerernummer);
        final boolean b = warenkorbService.ausDemCdWarenkorbEntfernen(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return Map.of("result", b);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortKurzDTO> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                               @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final CdWarenkorb cdWarenkorb = warenkorbService.cdWarenkorbKopie(
                bestellungService.bestellungSessionId(xHoerernummer),
                hoerernummer);
        final List<HoerbuchAntwortKurzDTO> hoerbuchAntwortKurzDTOS = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(cdWarenkorb.getTitelnummern()));
        hoerbuchAntwortKurzDTOS.forEach(hoerbuchAntwortKurzDTO -> {
            hoerbuchAntwortKurzDTO.setAufDerMerkliste(merklisteService.enthalten(hoerernummer,
                    new Titelnummer(hoerbuchAntwortKurzDTO.getTitelnummer())));
        });
        return hoerbuchAntwortKurzDTOS;
    }

    @PostMapping(value = "/bestellen",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> bestellen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                         @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                         @RequestBody final BestellungAnfrageDTO bestellungAnfrageDTO) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(xHoerernummer);
        return Map.of("bestellungId", bestellungService.bestellungAufgeben(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                Hoerername.of(bestellungAnfrageDTO.getHoerername()),
                new HoererEmail(bestellungAnfrageDTO.getHoereremail()),
                bestellungAnfrageDTO.getBemerkung(),
                bestellungAnfrageDTO.getBestellkarteMischen(),
                bestellungAnfrageDTO.getAlteBestellkarteLoeschen())
                .orElseThrow()
                .getValue());
    }

}
