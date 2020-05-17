package wbh.bookworm.hoerbuchdienst.domain.ports.mandant;

import wbh.bookworm.shared.domain.hoerer.MandantenId;

public interface MandantService {

    boolean existiert(MandantenId mandantenId);

    MandantDTO find(MandantenId mandantenId);

}
