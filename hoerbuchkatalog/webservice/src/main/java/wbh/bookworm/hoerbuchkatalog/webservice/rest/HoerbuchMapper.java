package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.shared.domain.AghNummer;
import wbh.bookworm.shared.domain.Sachgebiet;
import wbh.bookworm.shared.domain.Titelnummer;

@Mapper
public abstract class HoerbuchMapper {

    public static final HoerbuchMapper INSTANCE = Mappers.getMapper(HoerbuchMapper.class);

    abstract HoerbuchAntwortDTO convert(Hoerbuch hoerbuch);

    String map(Titelnummer value) {
        return value.getValue();
    }

    String map(Sachgebiet value) {
        return value.getName();
    }

    String map(AghNummer value) {
        return value.getValue();
    }

}
