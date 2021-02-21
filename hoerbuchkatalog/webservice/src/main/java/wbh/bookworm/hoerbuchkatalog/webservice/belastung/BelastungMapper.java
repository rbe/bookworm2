package wbh.bookworm.hoerbuchkatalog.webservice.belastung;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.shared.domain.Titelnummer;

@Mapper
public abstract class BelastungMapper {

    public static final BelastungMapper INSTANCE = Mappers.getMapper(BelastungMapper.class);

    abstract List<Belastung> convert(List<wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung> belastungen);

    String map(Titelnummer value) {
        return null != value ? value.getValue() : "";
    }

}
