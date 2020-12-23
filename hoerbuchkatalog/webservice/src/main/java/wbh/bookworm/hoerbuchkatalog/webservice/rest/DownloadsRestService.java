package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping(value = "{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                           @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                           @PathVariable final String titelnummer) {
        final boolean b = downloadsService.hinzufuegen(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        return b ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<HoerbuchAntwortKurzDTO>>> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                           @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Downloads downloads = downloadsService.downloadsKopie(hoerernummer);
        final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        final List<HoerbuchAntwortKurzDTO> hoerbuchAntwortKurzDTOS = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(downloads.getTitelnummern().keySet()));
        hoerbuchAntwortKurzDTOS.forEach(dto -> {
            dto.setAlsDownloadGebucht(true);
            final Titelnummer titelnummer = new Titelnummer(dto.getTitelnummer());
            dto.setAusgeliehenAm(format(downloads.ausgeliehenAm(titelnummer)));
            dto.setRueckgabeBis(format(downloads.rueckgabeBis(titelnummer)));
            dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            dto.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer));
        });
        return ResponseEntity.ok(new AntwortDTO<>(Map.of(), hoerbuchAntwortKurzDTOS));
    }

    @GetMapping(value = "datumab/{datumab}/stichwort/{stichwort}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<List<HoerbuchAntwortKurzDTO>>> inhaltGefiltert(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                                    @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                                                    @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                                                    @PathVariable final String datumab,
                                                                                    @PathVariable final String stichwort) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Downloads downloads = downloadsService.downloadsKopie(hoerernummer);
        final BestellungSessionId bestellungSessionId = BestellungSessionId.of(xBestellungSessionId);
        final List<HoerbuchAntwortKurzDTO> hoerbuchAntwortKurzDTOS = hoerbuchResolver.toHoerbuchAntwortKurzDTO(new ArrayList<>(downloads.getTitelnummern().keySet()));
        hoerbuchAntwortKurzDTOS.forEach(dto -> {
            dto.setAlsDownloadGebucht(true);
            final Titelnummer titelnummer = new Titelnummer(dto.getTitelnummer());
            dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            dto.setImWarenkorb(warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer));
        });
        return ResponseEntity.ok(new AntwortDTO<>(Map.of(), hoerbuchAntwortKurzDTOS));
    }

    private String format(final LocalDateTime localDateTime) {
        try {
            return localDateTime.format(DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return "";
        }
    }

}
