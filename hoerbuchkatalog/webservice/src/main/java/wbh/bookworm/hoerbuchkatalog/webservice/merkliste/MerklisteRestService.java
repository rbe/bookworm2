package wbh.bookworm.hoerbuchkatalog.webservice.merkliste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.webservice.api.Antwort;
import wbh.bookworm.hoerbuchkatalog.webservice.katalog.HoerbuchInfo;
import wbh.bookworm.hoerbuchkatalog.webservice.katalog.HoerbuchResolver;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Tag(name = "Merkliste", description = "")
@RestController
@RequestMapping("/v1/merkliste")
public class MerklisteRestService {

    private final DownloadsService downloadsService;

    private final MerklisteService merklisteService;

    private final WarenkorbService warenkorbService;

    private final HoerbuchResolver hoerbuchResolver;

    @Autowired
    public MerklisteRestService(final DownloadsService downloadsService,
                                final MerklisteService merklisteService,
                                final WarenkorbService warenkorbService,
                                final HoerbuchResolver hoerbuchResolver) {
        this.downloadsService = downloadsService;
        this.merklisteService = merklisteService;
        this.warenkorbService = warenkorbService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @PutMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                           @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                           @PathVariable final String titelnummer) {
        final boolean b = merklisteService.hinzufuegen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return b ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @DeleteMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> entfernen(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                          @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                          @PathVariable final String titelnummer) {
        final boolean b = merklisteService.entfernen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return b ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<List<HoerbuchInfo>>> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                              @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                              @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId;
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        } else {
            bestellungSessionId = null;
        }
        final List<HoerbuchInfo> hoerbuchInfos = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(merkliste.getTitelnummern()));
        hoerbuchInfos.forEach(dto -> {
            dto.setAufDerMerkliste(true);
            final Titelnummer titelnummer = new Titelnummer(dto.getTitelnummer());
            dto.setDownloadErlaubt(downloadsService.downloadErlaubt(hoerernummer, titelnummer));
            dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer));
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
    @GetMapping(value = "datumab/{datumab}/stichwort/{stichwort}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<List<HoerbuchInfo>>> inhaltGefiltert(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                       @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                       @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                                       @PathVariable final String datumab,
                                                                       @PathVariable final String stichwort) {
        final Merkliste merkliste = merklisteService.merklisteKopie(new Hoerernummer(xHoerernummer));
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        final List<HoerbuchInfo> hoerbuchInfos = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(merkliste.getTitelnummern()));
        hoerbuchInfos.forEach(dto -> {
            dto.setAufDerMerkliste(true);
            final Titelnummer titelnummer = new Titelnummer(dto.getTitelnummer());
            dto.setDownloadErlaubt(downloadsService.downloadErlaubt(hoerernummer, titelnummer));
            dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer));
            dto.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(
                    bestellungSessionId, hoerernummer, titelnummer));
        });
        final Map<String, Object> map = Map.of("count", hoerbuchInfos.size(),
                "stichwort", null != stichwort ? stichwort : "",
                "datumab", null != datumab ? datumab : "");
        return ResponseEntity.ok(new Antwort<>(map, hoerbuchInfos));
    }

}
