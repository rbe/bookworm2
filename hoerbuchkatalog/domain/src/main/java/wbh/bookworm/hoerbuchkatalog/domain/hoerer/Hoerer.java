/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.ddd.model.DomainAggregate;

import java.util.Objects;

public final class Hoerer extends DomainAggregate<Hoerer, Hoerernummer> {

    private Hoerername hoerername;

    private HoererEmail hoereremail;

    protected Hoerer(final Hoerernummer hoerernummer) {
        super(hoerernummer);
    }

    public Hoerer(final Hoerernummer hoerernummer,
                  final Hoerername hoerername, final HoererEmail hoereremail) {
        super(hoerernummer);
        this.hoerername = hoerername;
        this.hoereremail = hoereremail;
    }

    public Hoerernummer getHoerernummer() {
        return domainId;
    }

    public Vorname getVorname() {
        return hoerername.getVorname();
    }

    public Nachname getNachname() {
        return hoerername.getNachname();
    }

    public HoererEmail getHoereremail() {
        return hoereremail;
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainId);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Hoerer that = (Hoerer) other;
        return Objects.equals(that.domainId, this.domainId);
    }

    @Override
    public int compareTo(final Hoerer other) {
        return other.domainId.compareTo(this.domainId.getValue());
    }

    @Override
    public String toString() {
        return String.format("Hoerer{hoerernummer=%s, hoerername='%s', hoereremail='%s'}",
                domainId, hoerername, hoereremail);
    }

}
