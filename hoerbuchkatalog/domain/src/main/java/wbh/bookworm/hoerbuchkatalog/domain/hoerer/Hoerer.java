/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainId;

public class Hoerer extends DomainAggregate<Hoerer> {

    private final Hoerernummer hoerernummer;

    private String hoerername;

    private String hoereremail;

    protected Hoerer(final Hoerernummer hoerernummer) {
        super(new DomainId<>(hoerernummer.getValue()));
        this.hoerernummer = hoerernummer;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    @Override
    public int compareTo(final Hoerer o) {
        /* TODO Comparable */return 0;
    }

}
