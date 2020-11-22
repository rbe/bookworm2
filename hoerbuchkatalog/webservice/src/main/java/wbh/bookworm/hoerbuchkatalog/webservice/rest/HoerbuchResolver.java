package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Component
class HoerbuchResolver {

    private final HoerbuchkatalogService hoerbuchkatalogService;

    HoerbuchResolver(final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    List<HoerbuchAntwortDTO> toHoerbuchAntwortDTO(final List<Titelnummer> titelnummern) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        return titelnummern.stream()
                .map(t -> hoerbuchkatalogService.hole(hoerernummer, t))
                .map(h -> new HoerbuchAntwortDTO(h.getTitelnummer().getValue(),
                        h.getTitel(), h.getAutor()))
                .collect(Collectors.toUnmodifiableList());
    }

}
