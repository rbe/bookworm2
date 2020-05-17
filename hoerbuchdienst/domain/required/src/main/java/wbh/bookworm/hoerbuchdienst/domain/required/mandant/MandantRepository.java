package wbh.bookworm.hoerbuchdienst.domain.required.mandant;

import java.util.List;

import wbh.bookworm.shared.domain.hoerbuch.AghNummer;
import wbh.bookworm.shared.domain.hoerer.Hoerernummer;
import wbh.bookworm.shared.domain.hoerer.MandantenId;

public interface MandantRepository {

    boolean existiert(MandantenId mandantenId);

    Mandant find(MandantenId mandantenId);

    List<Hoerernummer> alleHoerernummern(MandantenId mandantenId);

    boolean hoerernummerExistiert(MandantenId mandantenId, Hoerernummer hoerernummer);

    List<AghNummer> alleAghNummern(MandantenId mandantenId);

    boolean AghNummerExistiert(MandantenId mandantenId, AghNummer aghNummer);

    // TODO String toMandantspezifischerDaisyName(MandantenId mandantenId, AghNummer aghNummer);

}
