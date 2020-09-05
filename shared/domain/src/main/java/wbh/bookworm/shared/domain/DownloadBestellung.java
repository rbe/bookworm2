package wbh.bookworm.shared.domain;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

/**
 * ValueObject
 */
public final class DownloadBestellung extends DomainValueObject {

    private static final long serialVersionUID = -9185860869816392952L;

    private final MandantenId mandantenId;

    private final Hoerernummer hoerernummer;

    private final AghNummer aghNummer;

    public DownloadBestellung(final MandantenId mandantenId,
                              final Hoerernummer hoerernummer,
                              final AghNummer aghNummer) {
        this.mandantenId = mandantenId;
        this.hoerernummer = hoerernummer;
        this.aghNummer = aghNummer;
    }

    public MandantenId getMandantenId() {
        return mandantenId;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public AghNummer getAghNummer() {
        return aghNummer;
    }

    @Override
    public String toString() {
        return String.format("Bestellung{mandant=%s, hoerernummer=%s, aghNummer=%s}",
                mandantenId, hoerernummer, aghNummer);
    }

}
