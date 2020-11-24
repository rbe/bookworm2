package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.shared.domain.Hoerernummer;

@RestController
@RequestMapping("/v1/hoererdaten")
public class HoererdatenRestService {

    private final HoererService hoererService;

    public HoererdatenRestService(final HoererService hoererService) {
        this.hoererService = hoererService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public HoererAntwortDTO suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                  @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Hoerer hoerer = hoererService.hoerer(new Hoerernummer(xHoerernummer));
        final HoererAntwortDTO hoererAntwortDTO = HoererMapper.INSTANCE.convert(hoerer);
        hoererAntwortDTO.setMandant("06");
        hoererAntwortDTO.setHoerernummer(xHoerernummer);
        return hoererAntwortDTO;
    }

}
