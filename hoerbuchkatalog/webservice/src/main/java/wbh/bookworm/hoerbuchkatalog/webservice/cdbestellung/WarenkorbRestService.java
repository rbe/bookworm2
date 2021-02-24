package wbh.bookworm.hoerbuchkatalog.webservice.cdbestellung;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import wbh.bookworm.hoerbuchkatalog.webservice.api.Antwort;
import wbh.bookworm.hoerbuchkatalog.webservice.katalog.HoerbuchInfo;
import wbh.bookworm.hoerbuchkatalog.webservice.katalog.HoerbuchResolver;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Tag(name = "Warenkorb", description = "CD-Bestellungen")
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

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
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

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
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

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<List<HoerbuchInfo>>> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                              @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                              @RequestHeader("X-Bookworm-BestellungSessionId") final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId;
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        } else {
            bestellungSessionId = null;
        }
        final CdWarenkorb cdWarenkorb = warenkorbService.cdWarenkorbKopie(bestellungSessionId, hoerernummer);
        final List<HoerbuchInfo> hoerbuchInfos = hoerbuchResolver
                .toHoerbuchAntwortKurzDTO(new ArrayList<>(cdWarenkorb.getTitelnummern()));
        hoerbuchInfos.forEach(dto -> {
            final Titelnummer titelnummer = new Titelnummer(dto.getTitelnummer());
            dto.setDownloadErlaubt(downloadsService.downloadErlaubt(hoerernummer, titelnummer));
            dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer));
            dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            final boolean imWarenkorb = null != bestellungSessionId
                    && warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer);
            dto.setImWarenkorb(imWarenkorb);
        });
        final Map<String, Object> map = Map.of("count", hoerbuchInfos.size());
        return ResponseEntity.ok(new Antwort<>(map, hoerbuchInfos));
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<Map<String, Object>>> bestellen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                  @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                  @RequestHeader("X-Bookworm-BestellungSessionId") final String xBestellungSessionId,
                                                                  @RequestBody final Bestellung bestellung) {
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(xBestellungSessionId);
        final Optional<BestellungId> bestellungId = bestellungService.bestellungAufgeben(bestellungSessionId,
                new Hoerernummer(xHoerernummer),
                Hoerername.of(bestellung.getHoerername()),
                new HoererEmail(bestellung.getHoereremail()),
                bestellung.getBemerkung(),
                bestellung.getBestellkarteMischen(),
                bestellung.getAlteBestellkarteLoeschen());
        final Map<String, Object> map = Map.of("bestellungId", bestellungId.orElseThrow().getValue());
        return ResponseEntity.ok(new Antwort<>(Map.of(), map));
    }

}
