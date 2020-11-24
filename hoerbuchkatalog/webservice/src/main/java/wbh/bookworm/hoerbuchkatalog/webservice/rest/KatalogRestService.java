package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@RestController
@RequestMapping("/v1/katalog")
public class KatalogRestService {

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final TitelnummerHoerbuchResolver titelnummerHoerbuchResolver;

    public KatalogRestService(final HoerbuchkatalogService hoerbuchkatalogService,
                              final TitelnummerHoerbuchResolver titelnummerHoerbuchResolver) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.titelnummerHoerbuchResolver = titelnummerHoerbuchResolver;
    }

    @GetMapping(value = "/stichwort/{stichwort}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HoerbuchAntwortDTO> suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                          @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                          @PathVariable("stichwort") final String stichwort) {
        final Suchparameter suchparameter = new Suchparameter();
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        final Hoerernummer hoerernummer = new Hoerernummer(xHoerernummer);
        final Suchergebnis suchergebnis = hoerbuchkatalogService.suchen(hoerernummer, suchparameter);
        return titelnummerHoerbuchResolver.toHoerbuchAntwortDTO(suchergebnis.getTitelnummern());
    }

    @GetMapping(value = "/details/{titelnummer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HoerbuchAntwortDTO hole(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                   @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer,
                                   @PathVariable("titelnummer") final String titelnummer) {
        final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer(xHoerernummer),
                new Titelnummer(titelnummer));
        final HoerbuchAntwortDTO hoerbuchAntwortDTO = HoerbuchMapper.INSTANCE.convert(hoerbuch);
        hoerbuchAntwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
        return hoerbuchAntwortDTO;
    }

}
