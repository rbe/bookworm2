package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Sachgebiet;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/katalog")
public class KatalogRestService {

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final MerklisteService merklisteService;

    private final WarenkorbService warenkorbService;

    private final BestellungService bestellungService;

    @Autowired
    public KatalogRestService(final HoerbuchkatalogService hoerbuchkatalogService,
                              final MerklisteService merklisteService,
                              final WarenkorbService warenkorbService,
                              final BestellungService bestellungService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.merklisteService = merklisteService;
        this.warenkorbService = warenkorbService;
        this.bestellungService = bestellungService;
    }

    @GetMapping(value = "/stichwort/{stichwort}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortKurzDTO> suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                              @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                              @PathVariable("stichwort") final String stichwort) {
        final Suchparameter suchparameter = new Suchparameter();
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        final Suchergebnis suchergebnis = hoerbuchkatalogService.suchen(hoerernummer, suchparameter);
        final List<HoerbuchAntwortKurzDTO> antwort = new ArrayList<>();
        for (Titelnummer titelnummer : suchergebnis.getTitelnummern()) {
            final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer, titelnummer);
            final HoerbuchAntwortKurzDTO hoerbuchAntwortKurzDTO = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
            final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
            if (null != sachgebiet) {
                hoerbuchAntwortKurzDTO.setSachgebiet(sachgebiet.getName());
                hoerbuchAntwortKurzDTO.setSachgebietBezeichnung(sachgebiet.getDescription());
            }
            hoerbuchAntwortKurzDTO.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            hoerbuchAntwortKurzDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer));
            antwort.add(hoerbuchAntwortKurzDTO);
        }
        return antwort;
    }

    @GetMapping(value = "/{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HoerbuchAntwortKurzDTO info(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                       @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                       @PathVariable("titelnummer") final String titelnummer) {
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer(xHoerernummer), titelnummer1);
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        final HoerbuchAntwortKurzDTO hoerbuchAntwortKurzDTO = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            hoerbuchAntwortKurzDTO.setSachgebiet(sachgebiet.getName());
            hoerbuchAntwortKurzDTO.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        hoerbuchAntwortKurzDTO.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        hoerbuchAntwortKurzDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        return hoerbuchAntwortKurzDTO;
    }

    @GetMapping(value = "/{titelnummer}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public HoerbuchAntwortDTO details(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                      @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                      @PathVariable("titelnummer") final String titelnummer) {
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer(xHoerernummer), titelnummer1);
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        final HoerbuchAntwortDTO hoerbuchAntwortDTO = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            hoerbuchAntwortDTO.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        hoerbuchAntwortDTO.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        hoerbuchAntwortDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        return hoerbuchAntwortDTO;
    }

}
