package wbh.bookworm.shared.domain.mandant;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

/**
 * ValueObject
 */
public final class HoererIdentifikation extends DomainValueObject {

    private static final long serialVersionUID = -9185860869816392952L;

    private final MandantenId mandantenId;

    private final Hoerernummer hoerernummer;

    public HoererIdentifikation(final MandantenId mandantenId,
                                final Hoerernummer hoerernummer) {
        this.mandantenId = mandantenId;
        this.hoerernummer = hoerernummer;
    }

    public MandantenId getMandantenId() {
        return mandantenId;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    @Override
    public String toString() {
        return String.format("HoererIdentifikation{mandant=%s, hoerernummer=%s}", mandantenId, hoerernummer);
    }

}
