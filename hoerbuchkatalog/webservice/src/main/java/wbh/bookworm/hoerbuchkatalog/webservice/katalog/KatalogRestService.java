package wbh.bookworm.hoerbuchkatalog.webservice.katalog;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.webservice.api.Antwort;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Sachgebiet;
import wbh.bookworm.shared.domain.Titelnummer;

@Tag(name = "Katalog", description = "")
@RestController
@RequestMapping("/v1/katalog")
public class KatalogRestService {

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final MerklisteService merklisteService;

    private final DownloadsService downloadsService;

    private final WarenkorbService warenkorbService;

    @Autowired
    public KatalogRestService(final HoerbuchkatalogService hoerbuchkatalogService,
                              final MerklisteService merklisteService,
                              final DownloadsService downloadsService,
                              final WarenkorbService warenkorbService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.merklisteService = merklisteService;
        this.downloadsService = downloadsService;
        this.warenkorbService = warenkorbService;
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(value = {
            "/stichwort/{stichwort}",
            "/sachgebiet/{sachgebiet}",
            "/einstelldatum/{einstelldatum}",
            "/titel/{titel}",
            "/autor/{autor}",
            "/stichwort/{stichwort}/sachgebiet/{sachgebiet}",
            "/stichwort/{stichwort}/einstelldatum/{einstelldatum}",
            "/stichwort/{stichwort}/sachgebiet/{sachgebiet}/einstelldatum/{einstelldatum}/titel/{titel}/autor/{autor}",
            "/sachgebiet/{sachgebiet}/einstelldatum/{einstelldatum}",
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<List<HoerbuchInfo>>> suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                             @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                             @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                             @PathVariable(value = "stichwort", required = false) final String stichwort,
                                                             @PathVariable(value = "sachgebiet", required = false) final String sachgebiet,
                                                             @PathVariable(value = "einstelldatum", required = false) final String einstelldatum,
                                                             @PathVariable(value = "titel", required = false) final String titel,
                                                             @PathVariable(value = "autor", required = false) final String autor) {
        final Suchparameter suchparameter = suchparameter(stichwort, sachgebiet, einstelldatum, titel, autor);
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Suchergebnis suchergebnis = hoerbuchkatalogService.suchen(hoerernummer, suchparameter);
        if (!suchergebnis.hatErgebnisse()) {
            return ResponseEntity.notFound().build();
        }
        final BestellungSessionId bestellungSessionId;
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        } else {
            bestellungSessionId = null;
        }
        final List<HoerbuchInfo> antwort = new ArrayList<>();
        for (final Titelnummer titelnummer : suchergebnis.getTitelnummern()) {
            final wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer, titelnummer);
            final HoerbuchInfo dto = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
            final Sachgebiet hbSachgebiet = hoerbuch.getSachgebiet();
            if (null != hbSachgebiet) {
                dto.setSachgebiet(hbSachgebiet.getName());
                dto.setSachgebietBezeichnung(hbSachgebiet.getDescription());
            }
            dto.setDownloadErlaubt(downloadsService.downloadErlaubt(hoerernummer, titelnummer));
            dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer));
            dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            final boolean imWarenkorb = null != bestellungSessionId
                    && warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer);
            dto.setImWarenkorb(imWarenkorb);
            antwort.add(dto);
        }
        final Map<String, Object> meta = Map.of(
                "stichwort", null != stichwort ? stichwort : "",
                "sachgebiet", null != sachgebiet ? sachgebiet : "",
                "sachgebietBezeichnung", null != sachgebiet && !sachgebiet.isBlank() && !"*".equals(sachgebiet)
                        ? Sachgebiet.valueOf(sachgebiet).getDescription()
                        : "",
                "einstelldatum", null != einstelldatum ? einstelldatum : "",
                "titel", null != titel ? titel : "",
                "autor", null != autor ? autor : "",
                "count", antwort.size());
        return !antwort.isEmpty()
                ? ResponseEntity.ok(new Antwort<>(meta, antwort))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(value = "/{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<HoerbuchInfo>> info(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                      @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                      @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                      @PathVariable("titelnummer") final String titelnummer) {
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        final wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer(xHoerernummer), titelnummer1);
        if (hoerbuch.isUnbekannt()) {
            return ResponseEntity.notFound().build();
        }
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final HoerbuchInfo dto = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            dto.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        dto.setDownloadErlaubt(downloadsService.downloadErlaubt(hoerernummer, titelnummer1));
        dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer1));
        dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
            dto.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        } else {
            dto.setImWarenkorb(false);
        }
        final Map<String, Object> meta = Map.of();
        return ResponseEntity.ok(new Antwort<>(meta, dto));
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(value = "/{titelnummer}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<Hoerbuch>> details(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                     @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                     @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                     @PathVariable("titelnummer") final String titelnummer) {
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        final wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer(xHoerernummer), titelnummer1);
        if (hoerbuch.isUnbekannt()) {
            return ResponseEntity.notFound().build();
        }
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Hoerbuch dto = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            dto.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        dto.setDownloadErlaubt(downloadsService.downloadErlaubt(hoerernummer, titelnummer1));
        dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer1));
        dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
            dto.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        } else {
            dto.setImWarenkorb(false);
        }
        return ResponseEntity.ok(new Antwort<>(Map.of(), dto));
    }

    private Suchparameter suchparameter(final String stichwort, final String sachgebiet, final String einstelldatum,
                                        final String titel, final String autor) {
        final Suchparameter suchparameter = new Suchparameter();
        if (null != stichwort && !stichwort.isBlank() && !stichwort.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
            suchparameter.setMaxAnzahlSuchergebnisse(1_000);
        }
        if (null != einstelldatum && !einstelldatum.isBlank() && !einstelldatum.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.EINSTELLDATUM, einstelldatum);
            suchparameter.setMaxAnzahlSuchergebnisse(1_000);
        }
        if (null != titel && !titel.isBlank() && !titel.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.TITEL, titel);
            suchparameter.setMaxAnzahlSuchergebnisse(1_000);
        }
        if (null != autor && !autor.isBlank() && !autor.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.AUTOR, autor);
            suchparameter.setMaxAnzahlSuchergebnisse(1_000);
        }
        if (null != sachgebiet && !sachgebiet.isBlank() && !sachgebiet.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.SACHGEBIET, sachgebiet);
            suchparameter.setMaxAnzahlSuchergebnisse(-1);
        }
        return suchparameter;
    }

}
