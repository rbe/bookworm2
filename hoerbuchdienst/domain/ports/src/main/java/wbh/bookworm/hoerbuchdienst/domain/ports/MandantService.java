package wbh.bookworm.hoerbuchdienst.domain.ports;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.MandantenId;

public interface MandantService {

    boolean kannBestellen(MandantenId mandantenId, Hoerernummer hoerernummer);

}
