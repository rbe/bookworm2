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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/merkliste")
public class MerklisteRestService {

    private final MerklisteService merklisteService;

    private final WarenkorbService warenkorbService;

    private final BestellungService bestellungService;

    private final HoerbuchResolver hoerbuchResolver;

    @Autowired
    public MerklisteRestService(final MerklisteService merklisteService,
                                final WarenkorbService warenkorbService,
                                final BestellungService bestellungService,
                                final HoerbuchResolver hoerbuchResolver) {
        this.merklisteService = merklisteService;
        this.warenkorbService = warenkorbService;
        this.bestellungService = bestellungService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    //@PutMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                          @PathVariable final String titelnummer) {
        final boolean b = merklisteService.hinzufuegen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return Map.of("result", b);
    }

    @DeleteMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                         @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                         @PathVariable final String titelnummer) {
        final boolean b = merklisteService.entfernen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return Map.of("result", b);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortKurzDTO> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                               @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        final List<HoerbuchAntwortKurzDTO> hoerbuchAntwortKurzDTOS = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(merkliste.getTitelnummern()));
        hoerbuchAntwortKurzDTOS.forEach(hoerbuchAntwortKurzDTO -> {
            hoerbuchAntwortKurzDTO.setAufDerMerkliste(true);
            hoerbuchAntwortKurzDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer,
                    new Titelnummer(hoerbuchAntwortKurzDTO.getTitelnummer())));
        });
        return hoerbuchAntwortKurzDTOS;
    }

    @GetMapping(value = "datumab/{datumab}/suchbegriff/{suchbegriff}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortKurzDTO> inhaltGefiltert(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                        @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                        @PathVariable final String datumab,
                                                        @PathVariable final String suchbegriff) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        final List<HoerbuchAntwortKurzDTO> hoerbuchAntwortKurzDTOS = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(merkliste.getTitelnummern()));
        hoerbuchAntwortKurzDTOS.forEach(hoerbuchAntwortKurzDTO -> {
            hoerbuchAntwortKurzDTO.setAufDerMerkliste(true);
            hoerbuchAntwortKurzDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer,
                    new Titelnummer(hoerbuchAntwortKurzDTO.getTitelnummer())));
        });
        return hoerbuchAntwortKurzDTOS;
    }

}
