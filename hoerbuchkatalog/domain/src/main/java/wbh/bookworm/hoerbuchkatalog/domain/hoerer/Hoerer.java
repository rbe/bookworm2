/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import wbh.bookworm.platform.ddd.model.DomainAggregate;

import java.util.Objects;

public class Hoerer extends DomainAggregate<Hoerer, Hoerernummer> {

    private final Hoerernummer hoerernummer;

    private String hoerername;

    private String hoereremail;

    protected Hoerer(final Hoerernummer hoerernummer) {
        super(hoerernummer);
        this.hoerernummer = hoerernummer;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public String getHoerername() {
        return hoerername;
    }

    public String getHoereremail() {
        return hoereremail;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerernummer);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Hoerer that = (Hoerer) other;
        return Objects.equals(that.hoerernummer, this.hoerernummer);
    }

    @Override
    public int compareTo(final Hoerer other) {
        return other.hoerernummer.compareTo(this.hoerernummer.getValue());
    }

    @Override
    public String toString() {
        return String.format("Hoerer{domainId=%s, hoerernummer=%s, hoerername='%s', hoereremail='%s'}",
                domainId, hoerernummer, hoerername, hoereremail);
    }

}
