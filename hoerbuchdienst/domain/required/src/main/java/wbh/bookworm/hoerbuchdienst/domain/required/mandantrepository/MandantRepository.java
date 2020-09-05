package wbh.bookworm.hoerbuchdienst.domain.required.mandantrepository;

import java.util.Optional;
import java.util.Set;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.MandantenId;

public interface MandantRepository {

    boolean existiert(MandantenId mandantenId);

    Optional<Mandant> find(MandantenId mandantenId);

    void fuegeHoererHinzu(MandantenId mandantenId, Hoerernummer hoerernummer);

    void fuegeHoererHinzu(MandantenId mandantenId, Set<Hoerernummer> hoerernummer);

    boolean hoerernummerExistiert(MandantenId mandantenId, Hoerernummer hoerernummer);

}
