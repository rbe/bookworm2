package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    public MerklisteRestService(final MerklisteService merklisteService, final HoerbuchResolver hoerbuchResolver) {
        this.merklisteService = merklisteService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                              @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                              @RequestBody final HoerbuchAnfrageDTO hoerbuchAnfrageDTO) {
        merklisteService.hinzufuegen(new Hoerernummer(xHoerernummer),
                new Titelnummer(hoerbuchAnfrageDTO.getTitelnummer()));
        return true;
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                             @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                             @RequestBody final HoerbuchAnfrageDTO hoerbuchAnfrageDTO) {
        merklisteService.entfernen(new Hoerernummer(xHoerernummer),
                new Titelnummer(hoerbuchAnfrageDTO.getTitelnummer()));
        return true;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortDTO> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        return hoerbuchResolver.toHoerbuchAntwortDTO(new ArrayList<>(merkliste.getTitelnummern()));
    }

}
