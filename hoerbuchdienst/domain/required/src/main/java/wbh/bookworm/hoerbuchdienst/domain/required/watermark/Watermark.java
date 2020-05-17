package wbh.bookworm.hoerbuchdienst.domain.required.watermark;

import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.hoerer.HoererIdentifikation;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

public final class Watermark extends DomainValueObject {

    private final HoererIdentifikation hoererIdentifikation;

    private final Titelnummer titelnummer;

    public Watermark(final HoererIdentifikation hoererIdentifikation, final Titelnummer titelnummer) {
        this.hoererIdentifikation = hoererIdentifikation;
        this.titelnummer = titelnummer;
    }

}
