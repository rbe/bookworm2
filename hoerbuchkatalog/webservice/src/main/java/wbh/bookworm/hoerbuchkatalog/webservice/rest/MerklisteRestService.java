package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/merkliste")
public class MerklisteRestService {

    private final MerklisteService merklisteService;

    private final HoerbuchResolver hoerbuchResolver;

    @Autowired
    public MerklisteRestService(final MerklisteService merklisteService,
                                final HoerbuchResolver hoerbuchResolver) {
        this.merklisteService = merklisteService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @CrossOrigin(origins = {"http://localhost:8080"})
    //@PutMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                          @PathVariable final String titelnummer) {
        merklisteService.hinzufuegen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return Map.of("result", "true");
    }

    @DeleteMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                         @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                         @PathVariable final String titelnummer) {
        merklisteService.entfernen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return Map.of("result", "true");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortKurzDTO> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                               @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        return hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(merkliste.getTitelnummern()));
    }

    @GetMapping(value = "datumab/{datumab}/suchbegriff/{suchbegriff}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortKurzDTO> inhaltGefiltert(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                        @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                        @PathVariable final String datumab,
                                                        @PathVariable final String suchbegriff) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        return hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(merkliste.getTitelnummern()));
    }

}
