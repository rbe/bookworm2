/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.mandantrepository;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.MandantenId;

public final class Mandant implements Serializable {

    private static final long serialVersionUID = -1L;

    private final MandantenId id;

    private final Set<Hoerernummer> hoerernummern;

    private String piracyInquiryUrl;

    public Mandant(final MandantenId id, final Set<Hoerernummer> hoerernummern) {
        this.id = id;
        this.hoerernummern = hoerernummern;
    }

    public Mandant(final Mandant mandant) {
        this(mandant.id, mandant.hoerernummern);
    }

    public MandantenId getId() {
        return id;
    }

    public Set<Hoerernummer> getHoerernummern() {
        return hoerernummern;
    }

    public boolean add(final Hoerernummer hoerernummer) {
        return hoerernummern.add(hoerernummer);
    }

    public boolean add(final Set<Hoerernummer> hoerernummer) {
        return hoerernummern.addAll(hoerernummer);
    }

    public boolean contains(final Hoerernummer hoerernummer) {
        return hoerernummern.contains(hoerernummer);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Mandant mandant = (Mandant) o;
        return id.equals(mandant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Mandant{%s}", id);
    }

}
