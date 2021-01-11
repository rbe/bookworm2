package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Component
class HoerbuchResolver {

    private static final Hoerernummer HOERERNUMMER = new Hoerernummer("00000");

    private final HoerbuchkatalogService hoerbuchkatalogService;

    HoerbuchResolver(final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    List<HoerbuchAntwortDTO> toHoerbuchAntwortDTO(final List<Titelnummer> titelnummern) {
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(HOERERNUMMER, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwortDto)
                .collect(Collectors.toList());
    }

    List<HoerbuchAntwortKurzDTO> toHoerbuchAntwortKurzDTO(final List<Titelnummer> titelnummern) {
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(HOERERNUMMER, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwortKurzDto)
                .sorted()
                .collect(Collectors.toList());
    }

    @SuppressWarnings("squid:S3864")
    List<BelastungAntwortDTO> toBelastungenAntwortDTO(final List<Belastung> belastungen) {
        final List<BelastungAntwortDTO> belastungAntwortDtos = BelastungMapper.INSTANCE.convert(belastungen);
        return belastungAntwortDtos.stream()
                .peek(antwortDTO -> {
                    final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(HOERERNUMMER, new Titelnummer(antwortDTO.getTitelnummer()));
                    antwortDTO.setAutor(hoerbuch.getAutor());
                    antwortDTO.setTitel(hoerbuch.getTitel());
                    antwortDTO.setSachgebiet(hoerbuch.getSachgebiet().getName());
                    antwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
                    antwortDTO.setSprecher1(hoerbuch.getSprecher1());
                    antwortDTO.setSprecher2(hoerbuch.getSprecher2());
                    antwortDTO.setSpieldauer(hoerbuch.getSpieldauer());
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("squid:S3864")
    List<BestellkarteAntwortDTO> toBestellkarteAntwortDTO(final List<Bestellkarte> bestellkarten) {
        final List<BestellkarteAntwortDTO> bestellkarteAntwortDtos = BestellkarteMapper.INSTANCE.convert(bestellkarten);
        return bestellkarteAntwortDtos.stream()
                .peek(antwortDTO -> {
                    final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(HOERERNUMMER, new Titelnummer(antwortDTO.getTitelnummer()));
                    antwortDTO.setAutor(hoerbuch.getAutor());
                    antwortDTO.setTitel(hoerbuch.getTitel());
                    antwortDTO.setSachgebiet(hoerbuch.getSachgebiet().getName());
                    antwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
                    antwortDTO.setSprecher1(hoerbuch.getSprecher1());
                    antwortDTO.setSprecher2(hoerbuch.getSprecher2());
                    antwortDTO.setSpieldauer(hoerbuch.getSpieldauer());
                })
                .collect(Collectors.toList());
    }

    @SuppressWarnings("squid:S3864")
    List<ErledigteBestellkarteAntwortDTO> toErledigteBestellkarteAntwortDTO(final List<ErledigteBestellkarte> erledigteBestellkarten) {
        final List<ErledigteBestellkarteAntwortDTO> erledigteBestellkarteAntwortDtos = ErledigteBestellkarteMapper.INSTANCE.convert(erledigteBestellkarten);
        return erledigteBestellkarteAntwortDtos.stream()
                .peek(antwortDTO -> {
                    final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(HOERERNUMMER, new Titelnummer(antwortDTO.getTitelnummer()));
                    antwortDTO.setAutor(hoerbuch.getAutor());
                    antwortDTO.setTitel(hoerbuch.getTitel());
                    antwortDTO.setSachgebiet(hoerbuch.getSachgebiet().getName());
                    antwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
                    antwortDTO.setSprecher1(hoerbuch.getSprecher1());
                    antwortDTO.setSprecher2(hoerbuch.getSprecher2());
                    antwortDTO.setSpieldauer(hoerbuch.getSpieldauer());
                })
                .collect(Collectors.toList());
    }

}
