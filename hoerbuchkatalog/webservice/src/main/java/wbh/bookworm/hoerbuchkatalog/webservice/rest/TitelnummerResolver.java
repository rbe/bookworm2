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
class TitelnummerResolver {

    private final HoerbuchkatalogService hoerbuchkatalogService;

    TitelnummerResolver(final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    List<HoerbuchAntwortDTO> toHoerbuchAntwortDTO(final List<Titelnummer> titelnummern) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(hoerernummer, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwortDto)
                .collect(Collectors.toUnmodifiableList());
    }

    List<HoerbuchAntwortKurzDTO> toHoerbuchAntwortKurzDTO(final List<Titelnummer> titelnummern) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        return titelnummern.stream()
                .map(titelnummer -> hoerbuchkatalogService.hole(hoerernummer, titelnummer))
                .map(HoerbuchMapper.INSTANCE::convertToHoerbuchAntwortKurzDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @SuppressWarnings("squid:S3864")
    List<BelastungAntwortDTO> toBelastungenAntwortDTO(final List<Belastung> belastungen) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        final List<BelastungAntwortDTO> belastungAntwortDtos = BelastungMapper.INSTANCE.convert(belastungen);
        return belastungAntwortDtos.stream()
                .peek(antwortDTO -> {
                    final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer, new Titelnummer(antwortDTO.getTitelnummer()));
                    antwortDTO.setAutor(hoerbuch.getAutor());
                    antwortDTO.setTitel(hoerbuch.getTitel());
                    antwortDTO.setSachgebiet(hoerbuch.getSachgebiet().getName());
                    antwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
                    antwortDTO.setSprecher1(hoerbuch.getSprecher1());
                    antwortDTO.setSpieldauer(hoerbuch.getSpieldauer());
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @SuppressWarnings("squid:S3864")
    List<BestellkarteAntwortDTO> toBestellkarteAntwortDTO(final List<Bestellkarte> bestellkarten) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        final List<BestellkarteAntwortDTO> bestellkarteAntwortDtos = BestellkarteMapper.INSTANCE.convert(bestellkarten);
        return bestellkarteAntwortDtos.stream()
                .peek(antwortDTO -> {
                    final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer, new Titelnummer(antwortDTO.getTitelnummer()));
                    antwortDTO.setAutor(hoerbuch.getAutor());
                    antwortDTO.setTitel(hoerbuch.getTitel());
                    antwortDTO.setSachgebiet(hoerbuch.getSachgebiet().getName());
                    antwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
                    antwortDTO.setSprecher1(hoerbuch.getSprecher1());
                    antwortDTO.setSpieldauer(hoerbuch.getSpieldauer());
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @SuppressWarnings("squid:S3864")
    List<ErledigteBestellkarteAntwortDTO> toErledigteBestellkarteAntwortDTO(final List<ErledigteBestellkarte> erledigteBestellkarten) {
        final Hoerernummer hoerernummer = new Hoerernummer("00000");
        final List<ErledigteBestellkarteAntwortDTO> erledigteBestellkarteAntwortDtos = ErledigteBestellkarteMapper.INSTANCE.convert(erledigteBestellkarten);
        return erledigteBestellkarteAntwortDtos.stream()
                .peek(antwortDTO -> {
                    final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer, new Titelnummer(antwortDTO.getTitelnummer()));
                    antwortDTO.setAutor(hoerbuch.getAutor());
                    antwortDTO.setTitel(hoerbuch.getTitel());
                    antwortDTO.setSachgebiet(hoerbuch.getSachgebiet().getName());
                    antwortDTO.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
                    antwortDTO.setSprecher1(hoerbuch.getSprecher1());
                    antwortDTO.setSpieldauer(hoerbuch.getSpieldauer());
                })
                .collect(Collectors.toUnmodifiableList());
    }

}
