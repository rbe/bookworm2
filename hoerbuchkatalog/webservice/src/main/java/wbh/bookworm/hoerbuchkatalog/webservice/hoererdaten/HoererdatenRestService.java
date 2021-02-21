package wbh.bookworm.hoerbuchkatalog.webservice.hoererdaten;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.webservice.api.Antwort;
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
    public ResponseEntity<Antwort<Hoerer>> suche(@RequestHeader("X-Bookworm-Mandant") final String xMandant,
                                                 @RequestHeader("X-Bookworm-Hoerernummer") final String xHoerernummer) {
        final wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer hoerer = hoererService.hoerer(new Hoerernummer(xHoerernummer));
        if (hoerer.isUnbekannt()) {
            return ResponseEntity.notFound().build();
        }
        final Hoerer hoererAntwortDTO = HoererdatenMapper.INSTANCE.convert(hoerer);
        hoererAntwortDTO.setMandant(/* TODO Mandantenspezifisch */"06");
        hoererAntwortDTO.setHoerernummer(xHoerernummer);
        return ResponseEntity.ok(new Antwort<>(Map.of(), hoererAntwortDTO));
    }

    @Mapper
    abstract static class HoererdatenMapper {

        public static final HoererdatenMapper INSTANCE = Mappers.getMapper(HoererdatenMapper.class);

        abstract Hoerer convert(wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer hoerer);

        String map(Hoerernummer value) {
            return null != value ? value.getValue() : "";
        }

        String map(Vorname value) {
            return null != value ? value.getValue() : "";
        }

        String map(Nachname value) {
            return null != value ? value.getValue() : "";
        }

        String map(HoererEmail value) {
            return null != value ? value.getValue() : "";
        }

    }

}
