package wbh.bookworm.hoerbuchkatalog.webservice.katalog;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Component
public class HoerbuchResolver {

    private static final Hoerernummer HOERERNUMMER = new Hoerernummer("00000");

    private final HoerbuchkatalogService hoerbuchkatalogService;

    public HoerbuchResolver(final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    public List<Hoerbuch> toHoerbuchAntwortDTO(final List<Titelnummer> titelnummern) {
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(HOERERNUMMER, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwortDto)
                .collect(Collectors.toList());
    }

    public List<HoerbuchInfo> toHoerbuchAntwortKurzDTO(final List<Titelnummer> titelnummern) {
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(HOERERNUMMER, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwortKurzDto)
                .sorted()
                .collect(Collectors.toList());
    }

}
