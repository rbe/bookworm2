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

import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsService;
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

    @GetMapping(value = {
            "/stichwort/{stichwort}",
            "/sachgebiet/{sachgebiet}",
            "/einstelldatum/{einstelldatum}",
            "/stichwort/{stichwort}/sachgebiet/{sachgebiet}",
            "/stichwort/{stichwort}/einstelldatum/{einstelldatum}",
            "/stichwort/{stichwort}/sachgebiet/{sachgebiet}/einstelldatum/{einstelldatum}",
            "/sachgebiet/{sachgebiet}/einstelldatum/{einstelldatum}",
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<HoerbuchAntwortKurzDTO>>> suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                          @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                                          @PathVariable(value = "stichwort", required = false) final String stichwort,
                                                                          @PathVariable(value = "sachgebiet", required = false) final String sachgebiet,
                                                                          @PathVariable(value = "einstelldatum", required = false) final String einstelldatum) {
        final Suchparameter suchparameter = new Suchparameter();
        if (null != stichwort && !stichwort.isBlank() && !stichwort.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
            suchparameter.setMaxAnzahlSuchergebnisse(1_000);
        }
        if (null != einstelldatum && !einstelldatum.isBlank() && !einstelldatum.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.EINSTELLDATUM, einstelldatum);
            suchparameter.setMaxAnzahlSuchergebnisse(1_000);
        }
        if (null != sachgebiet && !sachgebiet.isBlank() && !sachgebiet.equals("*")) {
            suchparameter.hinzufuegen(Suchparameter.Feld.SACHGEBIET, sachgebiet);
            suchparameter.setMaxAnzahlSuchergebnisse(-1);
        }
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Suchergebnis suchergebnis = hoerbuchkatalogService.suchen(hoerernummer, suchparameter);
        if (!suchergebnis.hatErgebnisse()) {
            return ResponseEntity.notFound().build();
        }
        final boolean downloadErlaubt = downloadsService.downloadErlaubt(hoerernummer);
        final BestellungSessionId bestellungSessionId;
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        } else {
            bestellungSessionId = null;
        }
        final List<HoerbuchAntwortKurzDTO> antwort = new ArrayList<>();
        for (final Titelnummer titelnummer : suchergebnis.getTitelnummern()) {
            final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer, titelnummer);
            final HoerbuchAntwortKurzDTO dto = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
            final Sachgebiet hbSachgebiet = hoerbuch.getSachgebiet();
            if (null != hbSachgebiet) {
                dto.setSachgebiet(hbSachgebiet.getName());
                dto.setSachgebietBezeichnung(hbSachgebiet.getDescription());
            }
            dto.setDownloadErlaubt(downloadErlaubt);
            dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            final boolean imWarenkorb = null != bestellungSessionId
                    && warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer);
            dto.setImWarenkorb(imWarenkorb);
            antwort.add(dto);
        }
        final Map<String, Object> meta = Map.of("stichwort", null != stichwort ? stichwort : "",
                "sachgebiet", null != sachgebiet ? sachgebiet : "",
                "sachgebietBezeichnung", null != sachgebiet && !sachgebiet.isBlank() && !"*".equals(sachgebiet)
                        ? Sachgebiet.valueOf(sachgebiet).getDescription()
                        : "",
                "einstelldatum", null != einstelldatum ? einstelldatum : "",
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
        final HoerbuchAntwortKurzDTO dto = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            dto.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        final boolean downloadErlaubt = downloadsService.downloadErlaubt(hoerernummer);
        dto.setDownloadErlaubt(downloadErlaubt);
        dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer1));
        dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
            dto.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        } else {
            dto.setImWarenkorb(false);
        }
        final Map<String, Object> meta = Map.of();
        return ResponseEntity.ok(new AntwortDTO<>(meta, dto));
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
        final HoerbuchAntwortDTO dto = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortDto(hoerbuch);
        final Sachgebiet sachgebiet = hoerbuch.getSachgebiet();
        if (null != sachgebiet) {
            dto.setSachgebietBezeichnung(sachgebiet.getDescription());
        }
        final boolean downloadErlaubt = downloadsService.downloadErlaubt(hoerernummer);
        dto.setDownloadErlaubt(downloadErlaubt);
        dto.setAlsDownloadGebucht(downloadsService.enthalten(hoerernummer, titelnummer1));
        dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer1));
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
            dto.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer1));
        } else {
            dto.setImWarenkorb(false);
        }
        return ResponseEntity.ok(new AntwortDTO<>(Map.of(), dto));
    }

}
