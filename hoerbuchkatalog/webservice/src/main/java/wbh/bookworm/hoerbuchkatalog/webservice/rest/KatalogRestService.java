package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungSessionId;
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

    @Autowired
    public KatalogRestService(final HoerbuchkatalogService hoerbuchkatalogService,
                              final MerklisteService merklisteService,
                              final WarenkorbService warenkorbService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.merklisteService = merklisteService;
        this.warenkorbService = warenkorbService;
    }

    @GetMapping(value = "/stichwort/{stichwort}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<HoerbuchAntwortKurzDTO>>> suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                          @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                                          @PathVariable("stichwort") final String stichwort) {
        final Suchparameter suchparameter = new Suchparameter();
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        final Suchergebnis suchergebnis = hoerbuchkatalogService.suchen(hoerernummer, suchparameter);
        if (!suchergebnis.hatErgebnisse()) {
            return ResponseEntity.notFound().build();
        }
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
            if (bestellungSessionId.isBekannt()) {
                hoerbuchAntwortKurzDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer));
            } else {
                hoerbuchAntwortKurzDTO.setImWarenkorb(false);
            }
            antwort.add(hoerbuchAntwortKurzDTO);
        }
        final Map<String, Object> meta = Map.of("stichwort", stichwort,
                "count", antwort.size());
        return !antwort.isEmpty()
                ? ResponseEntity.ok(new AntwortDTO<>(meta, antwort))
                : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<HoerbuchAntwortKurzDTO>> info(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                   @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                   @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                                   @PathVariable("titelnummer") final String titelnummer) {
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer(xHoerernummer), titelnummer1);
        if (hoerbuch.isUnbekannt()) {
            return ResponseEntity.notFound().build();
        }
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        final HoerbuchAntwortKurzDTO hoerbuchAntwortKurzDTO = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            hoerbuchAntwortKurzDTO.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        hoerbuchAntwortKurzDTO.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        if (bestellungSessionId.isBekannt()) {
            hoerbuchAntwortKurzDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        } else {
            hoerbuchAntwortKurzDTO.setImWarenkorb(false);
        }
        final Map<String, Object> meta = Map.of();
        return ResponseEntity.ok(new AntwortDTO<>(meta, hoerbuchAntwortKurzDTO));
    }

    @GetMapping(value = "/{titelnummer}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<HoerbuchAntwortDTO>> details(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                  @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                  @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                                  @PathVariable("titelnummer") final String titelnummer) {
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer(xHoerernummer), titelnummer1);
        if (hoerbuch.isUnbekannt()) {
            return ResponseEntity.notFound().build();
        }
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        final HoerbuchAntwortDTO hoerbuchAntwortDTO = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            hoerbuchAntwortDTO.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        hoerbuchAntwortDTO.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        if (bestellungSessionId.isBekannt()) {
            hoerbuchAntwortDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        } else {
            hoerbuchAntwortDTO.setImWarenkorb(false);
        }
        return ResponseEntity.ok(new AntwortDTO<>(Map.of(), hoerbuchAntwortDTO));
    }

}
