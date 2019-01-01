/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.ddd.model.DomainAggregate;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
        Objects.requireNonNull(hoerername);
        this.hoerername = hoerername;
        Objects.requireNonNull(hoereremail);
        this.hoereremail = hoereremail;
    }

    @JsonIgnore
    public boolean isUnbekannt() {
        return domainId.isUnbekannt();
    }

    @JsonIgnore
    public boolean isBekannt() {
        return domainId.isBekannt();
    }

    public Hoerernummer getHoerernummer() {
        return domainId;
    }

    public boolean hasHoerername() {
        return hoerername.irgendeinNameVorhanden();
    }

    public Hoerername getHoerername() {
        return hoerername;
    }

    public boolean hasVorname() {
        return null != hoerername.getVorname();
    }

    public Vorname getVorname() {
        return hoerername.getVorname();
    }

    public boolean hasNachname() {
        return null != hoerername.getNachname();
    }

    public Nachname getNachname() {
        return hoerername.getNachname();
    }

    public String getName() {
        return String.format("%s %s",
                hasVorname() ? getVorname() : "Unbekannt",
                hasNachname() ? getNachname() : "Unbekannt");
    }

    public boolean hasHoereremail() {
        return null != hoereremail;
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
