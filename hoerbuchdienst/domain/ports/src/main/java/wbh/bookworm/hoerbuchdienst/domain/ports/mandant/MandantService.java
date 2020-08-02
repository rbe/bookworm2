package wbh.bookworm.hoerbuchdienst.domain.ports.mandant;

import wbh.bookworm.shared.domain.mandant.MandantenId;

public interface MandantService {

    boolean existiert(MandantenId mandantenId);

    MandantDTO find(MandantenId mandantenId);

}
