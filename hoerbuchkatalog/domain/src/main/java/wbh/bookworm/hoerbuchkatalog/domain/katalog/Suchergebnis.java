/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import aoc.ddd.model.DomainValueObject;

import java.util.List;

/**
 * Value Object
 */
public final class Suchergebnis extends DomainValueObject {

    private static final long serialVersionUID = -1L;

    private final Suchparameter suchparameter;

    private final List<Titelnummer> titelnummern;

    private final int anzahl;

    private final int gesamtAnzahlTreffer;

    public Suchergebnis(final Suchparameter suchparameter,
                        final List<Titelnummer> titelnummern,
                        final int gesamtAnzahlTreffer) {
        this.suchparameter = suchparameter;
        this.titelnummern = titelnummern;
        anzahl = titelnummern.size();
        this.gesamtAnzahlTreffer = gesamtAnzahlTreffer;
    }

    public Suchparameter getSuchparameter() {
        return suchparameter;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public int getGesamtAnzahlTreffer() {
        return gesamtAnzahlTreffer;
    }

    public List<Titelnummer> getTitelnummern() {
        return titelnummern;
    }

    public boolean vorherigeVorhanden(final Titelnummer titelnummer) {
        return titelnummern.indexOf(titelnummer) > -1;
    }

    public Titelnummer vorherige(final Titelnummer titelnummer) {
        int curIdx = titelnummern.indexOf(titelnummer);
        return titelnummern.get(curIdx - 1);
    }

    public boolean naechsteVorhanden(final Titelnummer titelnummer) {
        return titelnummern.indexOf(titelnummer) < titelnummern.size() - 1;
    }

    public Titelnummer naechste(final Titelnummer titelnummer) {
        int curIdx = titelnummern.indexOf(titelnummer);
        return titelnummern.get(curIdx + 1);
    }

    @Override
    public String toString() {
        return String.format("Suchergebnis{suchparameter=%s, titelnummern=%d, anzahl=%d, gesamtAnzahlTreffer=%d}",
                suchparameter, titelnummern.size(), anzahl, gesamtAnzahlTreffer);
    }

}
