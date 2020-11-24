package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    private final TitelnummerHoerbuchResolver titelnummerHoerbuchResolver;

    public MerklisteRestService(final MerklisteService merklisteService, final TitelnummerHoerbuchResolver titelnummerHoerbuchResolver) {
        this.merklisteService = merklisteService;
        this.titelnummerHoerbuchResolver = titelnummerHoerbuchResolver;
    }

    //@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                          @RequestBody final HoerbuchAnfrageDTO hoerbuchAnfrageDTO) {
        merklisteService.hinzufuegen(new Hoerernummer(xHoerernummer),
                new Titelnummer(hoerbuchAnfrageDTO.getTitelnummer()));
        return Map.of("result", "true");
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                         @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                         @RequestBody final HoerbuchAnfrageDTO hoerbuchAnfrageDTO) {
        merklisteService.entfernen(new Hoerernummer(xHoerernummer),
                new Titelnummer(hoerbuchAnfrageDTO.getTitelnummer()));
        return Map.of("result", "true");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortDTO> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        return titelnummerHoerbuchResolver.toHoerbuchAntwortDTO(new ArrayList<>(merkliste.getTitelnummern()));
    }

}
