package wbh.bookworm.hoerbuchkatalog.webservice.katalog;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.shared.domain.AghNummer;
import wbh.bookworm.shared.domain.Sachgebiet;
import wbh.bookworm.shared.domain.Titelnummer;

@Mapper
public abstract class HoerbuchMapper {

    public static final HoerbuchMapper INSTANCE = Mappers.getMapper(HoerbuchMapper.class);

    @Mapping(target = "produktionsjahr", source = "prodJahr")
    @Mapping(target = "produktionsort", source = "prodOrt")
    public abstract HoerbuchAntwortDTO convertToHoerbuchAntwortDto(Hoerbuch hoerbuch);

    public abstract HoerbuchAntwortKurzDTO convertToHoerbuchAntwortKurzDto(Hoerbuch hoerbuch);

    public String map(Titelnummer value) {
        return null != value ? value.getValue() : "";
    }

    public String map(Sachgebiet value) {
        return null != value ? value.getName() : "";
    }

    @AfterMapping
    public void setSachgebietBezeichnung(@MappingTarget HoerbuchAntwortKurzDTO dto, Hoerbuch hoerbuch) {
        dto.setSachgebietBezeichnung(hoerbuch.getSachgebiet().getDescription());
    }

    public String map(AghNummer value) {
        return null != value ? value.getValue() : "";
    }

}
