package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.shared.domain.Titelnummer;

@Mapper
public abstract class BelastungMapper {

    public static final BelastungMapper INSTANCE = Mappers.getMapper(BelastungMapper.class);

    abstract List<BelastungAntwortDTO> convert(List<Belastung> belastungen);

    String map(Titelnummer value) {
        return null != value ? value.getValue() : "";
    }

}
