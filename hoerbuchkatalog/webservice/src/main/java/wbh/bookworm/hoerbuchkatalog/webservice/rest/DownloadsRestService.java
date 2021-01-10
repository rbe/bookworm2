package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/downloads")
public class DownloadsRestService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final DownloadsService downloadsService;

    private final MerklisteService merklisteService;

    private final WarenkorbService warenkorbService;

    private final HoerbuchResolver hoerbuchResolver;

    @Autowired
    public DownloadsRestService(final DownloadsService downloadsService,
                                final MerklisteService merklisteService,
                                final WarenkorbService warenkorbService,
                                final HoerbuchResolver hoerbuchResolver) {
        this.downloadsService = downloadsService;
        this.merklisteService = merklisteService;
        this.warenkorbService = warenkorbService;
        this.hoerbuchResolver = hoerbuchResolver;
    }

    @PutMapping(value = "/{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                           @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                           @PathVariable final String titelnummer) {
        final boolean b = downloadsService.hinzufuegen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return b ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping(value = "/erlaubt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> downloadErlaubt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        return downloadsService.downloadErlaubt(hoerernummer)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping(value = "/heute", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> anzahlHeute(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                            @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                            @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Downloads downloads = downloadsService.downloadsKopie(hoerernummer);
        return ResponseEntity.ok(downloads.anzahlHeute());
    }

    @GetMapping(value = "/aktuellermonat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> anzahlAktuellerMonat(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                     @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                     @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Downloads downloads = downloadsService.downloadsKopie(hoerernummer);
        return ResponseEntity.ok(downloads.anzahlAktuellerMonat());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<HoerbuchAntwortKurzDTO>>> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                           @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Downloads downloads = downloadsService.downloadsKopie(hoerernummer);
        final BestellungSessionId bestellungSessionId;
        if (null != xBestellungSessionId && !xBestellungSessionId.isBlank()) {
            bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        } else {
            bestellungSessionId = null;
        }
        final List<HoerbuchAntwortKurzDTO> hoerbuchAntwortKurzDTOS = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(downloads.getTitelnummern().keySet()));
        final boolean downloadErlaubt = downloadsService.downloadErlaubt(hoerernummer);
        hoerbuchAntwortKurzDTOS.forEach(dto -> {
            dto.setDownloadErlaubt(downloadErlaubt);
            dto.setAlsDownloadGebucht(true);
            final Titelnummer titelnummer = new Titelnummer(dto.getTitelnummer());
            dto.setAusgeliehenAm(format(downloads.ausgeliehenAm(titelnummer)));
            dto.setRueckgabeBis(format(downloads.rueckgabeBis(titelnummer)));
            dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            final boolean imWarenkorb = null != bestellungSessionId
                    && warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer);
            dto.setImWarenkorb(imWarenkorb);
        });
        hoerbuchAntwortKurzDTOS.sort(Comparator.reverseOrder());
        final Map<String, Object> meta = Map.of("anzahlMonat", downloadsService.anzahlAktuellerMonat(hoerernummer),
                "anzahlHeute", downloadsService.anzahlHeute(hoerernummer));
        return ResponseEntity.ok(new AntwortDTO<>(meta, hoerbuchAntwortKurzDTOS));
    }

    private String format(final LocalDateTime localDateTime) {
        String ret = "";
        if (null != localDateTime) {
            try {
                ret = localDateTime.format(DATE_TIME_FORMATTER);
            } catch (Exception e) {
                // ignore
            }
        }
        return ret;
    }

}
