package wbh.bookworm.hoerbuchkatalog.webservice.bestellkarte;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Mapper
public abstract class BestellkarteMapper {

    public static final BestellkarteMapper INSTANCE = Mappers.getMapper(BestellkarteMapper.class);

    abstract List<Bestellkarte> convert(List<wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte> belastungen);

    String map(Hoerernummer value) {
        return null != value ? value.getValue() : "";
    }

    String map(Titelnummer value) {
        return null != value ? value.getValue() : "";
    }

}
