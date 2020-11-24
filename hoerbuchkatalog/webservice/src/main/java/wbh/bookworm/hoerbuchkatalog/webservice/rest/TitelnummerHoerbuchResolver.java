package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Component
class TitelnummerHoerbuchResolver {

    private final HoerbuchkatalogService hoerbuchkatalogService;

    TitelnummerHoerbuchResolver(final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    List<HoerbuchAntwortDTO> toHoerbuchAntwortDTO(final List<Titelnummer> titelnummern) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(hoerernummer, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwort)
                .collect(Collectors.toUnmodifiableList());
    }

    List<HoerbuchAntwortKurzDTO> toHoerbuchAntwortKurzDTO(final List<Titelnummer> titelnummern) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(hoerernummer, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwortKurz)
                .collect(Collectors.toUnmodifiableList());
    }

}
