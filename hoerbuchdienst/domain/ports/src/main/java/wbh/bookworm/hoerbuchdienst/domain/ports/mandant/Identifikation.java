package wbh.bookworm.hoerbuchdienst.domain.ports.mandant;

import java.io.Serializable;

// TODO Siehe HoererIdentifikation
public final class Identifikation implements Serializable {

    private static final long serialVersionUID = 3890182768661645205L;

    private final String mandant;

    private final String hoerernummer;

    public Identifikation(final String mandant, final String hoerernummer) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
    }

    public String getMandant() {
        return mandant;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

}
