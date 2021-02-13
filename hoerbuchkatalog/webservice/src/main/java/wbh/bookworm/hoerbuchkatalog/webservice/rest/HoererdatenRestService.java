package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.shared.domain.Hoerernummer;

@Tag(name = "Hörerdaten", description = "")
@RestController
@RequestMapping("/v1/hoererdaten")
public class HoererdatenRestService {

    private final HoererService hoererService;

    public HoererdatenRestService(final HoererService hoererService) {
        this.hoererService = hoererService;
    }

    @Operation(summary = "", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AntwortDTO<HoererAntwortDTO>> suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                              @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final Hoerer hoerer = hoererService.hoerer(new Hoerernummer(xHoerernummer));
        if (hoerer.isUnbekannt()) {
            return ResponseEntity.notFound().build();
        }
        final HoererAntwortDTO hoererAntwortDTO = HoererMapper.INSTANCE.convert(hoerer);
        hoererAntwortDTO.setMandant("06");
        hoererAntwortDTO.setHoerernummer(xHoerernummer);
        return ResponseEntity.ok(new AntwortDTO<>(Map.of(), hoererAntwortDTO));
    }

}
