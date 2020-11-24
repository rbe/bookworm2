package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.shared.domain.Hoerernummer;

@Mapper
public abstract class HoererMapper {

    public static final HoererMapper INSTANCE = Mappers.getMapper(HoererMapper.class);

    abstract HoererAntwortDTO convert(Hoerer hoerer);

    String map(Hoerernummer value) {
        return null != value ? value.getValue() : "";
    }

    String map(Vorname value) {
        return null != value ? value.getValue() : "";
    }

    String map(Nachname value) {
        return null != value ? value.getValue() : "";
    }

    String map(HoererEmail value) {
        return null != value ? value.getValue() : "";
    }

}
