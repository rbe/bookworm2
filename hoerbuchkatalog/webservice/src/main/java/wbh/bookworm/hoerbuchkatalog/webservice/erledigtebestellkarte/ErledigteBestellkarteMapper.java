package wbh.bookworm.hoerbuchkatalog.webservice.erledigtebestellkarte;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Mapper
public abstract class ErledigteBestellkarteMapper {

    public static final ErledigteBestellkarteMapper INSTANCE = Mappers.getMapper(ErledigteBestellkarteMapper.class);

    abstract List<ErledigteBestellkarte> convert(List<wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte> erledigteBestellkarten);

    String map(Hoerernummer value) {
        return null != value ? value.getValue() : "";
    }

    String map(Titelnummer value) {
        return null != value ? value.getValue() : "";
    }

}
