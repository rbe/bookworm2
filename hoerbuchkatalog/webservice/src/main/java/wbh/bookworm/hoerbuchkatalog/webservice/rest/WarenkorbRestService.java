package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/warenkorb")
public final class WarenkorbRestService {

    private final DownloadsService downloadsService;

    private final WarenkorbService warenkorbService;

    private final BestellungService bestellungService;

    private final MerklisteService merklisteService;

    private final HoerbuchResolver hoerbuchResolver;

    @Autowired
    public WarenkorbRestService(final DownloadsService downloadsService,
                                final WarenkorbService warenkorbService,
                                final BestellungService bestellungService,
                                final MerklisteService merklisteService,
                                final HoerbuchResolver hoerbuchResolver) {
        this.downloadsService = downloadsService;
        this.warenkorbService = warenkorbService;
        this.bestellungService = bestellungService;
        this.merklisteService = merklisteService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @PutMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                           @RequestHeader("X-Bookworm-BestellungSessionId") final String xBestellungSessionId,
                                           @PathVariable final String titelnummer) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(xBestellungSessionId);
        final boolean b = warenkorbService.inDenCdWarenkorb(bestellungSessionId,
                new Hoerernummer(xHoerernummer), new Titelnummer(titelnummer));
        return b ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @DeleteMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                          @RequestHeader("X-Bookworm-BestellungSessionId") final String xBestellungSessionId,
                                          @PathVariable final String titelnummer) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(xBestellungSessionId);
        final boolean b = warenkorbService.ausDemCdWarenkorbEntfernen(bestellungSessionId,
                new Hoerernummer(xHoerernummer), new Titelnummer(titelnummer));
        return b ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<HoerbuchAntwortKurzDTO>>> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                           @RequestHeader("X-Bookworm-BestellungSessionId") final String xBestellungSessionId) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(xBestellungSessionId);
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final CdWarenkorb cdWarenkorb = warenkorbService.cdWarenkorbKopie(bestellungSessionId, hoerernummer);
        final List<HoerbuchAntwortKurzDTO> hoerbuchAntwortKurzDTOS = hoerbuchResolver
                .toHoerbuchAntwortKurzDTO(new ArrayList<>(cdWarenkorb.getTitelnummern()));
        hoerbuchAntwortKurzDTOS.forEach(hoerbuchAntwortKurzDTO -> {
            final Titelnummer titelnummer = new Titelnummer(hoerbuchAntwortKurzDTO.getTitelnummer());
            hoerbuchAntwortKurzDTO.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer));
            hoerbuchAntwortKurzDTO.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            hoerbuchAntwortKurzDTO.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(
                    bestellungSessionId, hoerernummer, titelnummer));
        });
        return ResponseEntity.ok(new AntwortDTO<>(Map.of(), hoerbuchAntwortKurzDTOS));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<Map<String, Object>>> bestellen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                     @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                     @RequestHeader("X-Bookworm-BestellungSessionId") final String xBestellungSessionId,
                                                                     @RequestBody final BestellungAnfrageDTO bestellungAnfrageDTO) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(xBestellungSessionId);
        final Optional<BestellungId> bestellungId = bestellungService.bestellungAufgeben(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                Hoerername.of(bestellungAnfrageDTO.getHoerername()),
                new HoererEmail(bestellungAnfrageDTO.getHoereremail()),
                bestellungAnfrageDTO.getBemerkung(),
                bestellungAnfrageDTO.getBestellkarteMischen(),
                bestellungAnfrageDTO.getAlteBestellkarteLoeschen());
        final Map<String, Object> map = Map.of("bestellungId", bestellungId.orElseThrow().getValue());
        return ResponseEntity.ok(new AntwortDTO<>(Map.of(), map));
    }

}
