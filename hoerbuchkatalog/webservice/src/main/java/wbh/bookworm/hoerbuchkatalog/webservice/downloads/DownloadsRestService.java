package wbh.bookworm.hoerbuchkatalog.webservice.downloads;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import wbh.bookworm.hoerbuchkatalog.webservice.api.Antwort;
import wbh.bookworm.hoerbuchkatalog.webservice.katalog.HoerbuchInfo;
import wbh.bookworm.hoerbuchkatalog.webservice.katalog.HoerbuchResolver;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Tag(name = "Downloads", description = "")
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

    @Operation(summary = "Download vermerken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "403", description = "Download nicht erlaubt")
    })
    @PutMapping(value = "/{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> fuegeHinzu(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                           @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                           @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                           @PathVariable final String titelnummer) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        return downloadsService.ausleihen(hoerernummer, titelnummer1)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(summary = "Sind Downloads aktuell erlaubt?")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "403", description = "Downloads nicht erlaubt")
    })
    @GetMapping(value = "/erlaubt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> neuerDownloadErlaubt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                     @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                     @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        return downloadsService.neueBestellungErlaubt(hoerernummer)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(summary = "Download dieses Titels erlaubt?")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ""),
            @ApiResponse(responseCode = "403", description = "Download nicht erlaubt")
    })
    @GetMapping(value = "/{titelnummer}/erlaubt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> downloadTitelErlaubt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                     @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                     @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId,
                                                     @PathVariable final String titelnummer) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Titelnummer titelnummer1 = new Titelnummer(titelnummer);
        return downloadsService.downloadErlaubt(hoerernummer, titelnummer1)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(summary = "Anzahl heutiger Bestellungen von Hörbüchern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(value = "/heute", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> anzahlHeute(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                            @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                            @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Downloads downloads = downloadsService.downloadsKopie(hoerernummer);
        return ResponseEntity.ok(downloads.anzahlHeutigerBestellungen());
    }

    @Operation(summary = "Anzahl Bestellungen von Hörbüchern im aktuellen Ausleihzeitraum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(value = "/ausleihzeitraum", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> ausleihzeitraum(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                                @RequestHeader(value = "X-Bookworm-BestellungSessionId", required = false) final String xBestellungSessionId) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Downloads downloads = downloadsService.downloadsKopie(hoerernummer);
        return ResponseEntity.ok(downloads.anzahlBestellungenImAusleihzeitraum());
    }

    @Operation(summary = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Antwort<List<HoerbuchInfo>>> inhalt(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
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
        final List<HoerbuchInfo> hoerbuchInfos = hoerbuchResolver.toHoerbuchAntwortKurzDTO(
                new ArrayList<>(downloads.titelnummernImAusleihzeitraum().keySet()));
        hoerbuchInfos.forEach(dto -> {
            final Titelnummer titelnummer = new Titelnummer(dto.getTitelnummer());
            dto.setDownloadErlaubt(downloadsService.downloadErlaubt(hoerernummer, titelnummer));
            dto.setAlsDownloadGebucht(true);
            dto.setAnzahlDownloads(downloadsService.anzahlDownloads(hoerernummer, titelnummer));
            dto.setAusgeliehenAm(format(downloads.ausgeliehenAm(titelnummer)));
            dto.setRueckgabeBis(format(downloads.rueckgabeBis(titelnummer)));
            dto.setAufDerMerkliste(merklisteService.enthalten(hoerernummer, titelnummer));
            final boolean imWarenkorb = null != bestellungSessionId
                    && warenkorbService.imCdWarenkorbEnthalten(bestellungSessionId, hoerernummer, titelnummer);
            dto.setImWarenkorb(imWarenkorb);
        });
        hoerbuchInfos.sort(Comparator.<HoerbuchInfo, LocalDate>comparing(
                dto -> LocalDate.parse(dto.getAusgeliehenAm(), DATE_TIME_FORMATTER))
                .reversed());
        final Map<String, Object> meta = Map.of("anzahlAusleihzeitraum", downloadsService.anzahlAusleihzeitraum(hoerernummer),
                "anzahlHeute", downloadsService.anzahlHeute(hoerernummer));
        return ResponseEntity.ok(new Antwort<>(meta, hoerbuchInfos));
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
