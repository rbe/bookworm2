package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungSessionId;
import wbh.bookworm.shared.domain.Hoerernummer;

@RestController
@RequestMapping("/v1/session")
public final class SessionRestService {

    private final BestellungService bestellungService;

    @Autowired
    public SessionRestService(final BestellungService bestellungService) {
        this.bestellungService = bestellungService;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> bestellungSessionId(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                                   @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final BestellungSessionId bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        return ResponseEntity.ok(Map.of("bestellungSessionId", bestellungSessionId.getValue()));
    }

}
