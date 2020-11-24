package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Mapper
public abstract class ErledigteBestellkarteMapper {

    public static final ErledigteBestellkarteMapper INSTANCE = Mappers.getMapper(ErledigteBestellkarteMapper.class);

    abstract List<ErledigteBestellkarteAntwortDTO> convert(List<ErledigteBestellkarte> erledigteBestellkarten);

    String map(Hoerernummer value) {
        return value.getValue();
    }

    String map(Titelnummer value) {
        return value.getValue();
    }

}
