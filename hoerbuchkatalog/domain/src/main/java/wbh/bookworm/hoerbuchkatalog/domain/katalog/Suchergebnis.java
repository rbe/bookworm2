/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import wbh.bookworm.platform.ddd.model.DomainEntity;

import java.util.List;

/**
 * Entity
 */
public final class Suchergebnis extends DomainEntity<Suchergebnis> {

    private static final long serialVersionUID = -1L;

    private Suchparameter suchparameter;

    private List<Titelnummer> titelnummern;

    private int anzahl;

    public Suchergebnis(final Suchparameter suchparameter,
                        final List<Titelnummer> titelnummern) {
        this.suchparameter = suchparameter;
        this.titelnummern = titelnummern;
        anzahl = titelnummern.size();
    }

    public Suchparameter getSuchparameter() {
        return suchparameter;
    }

    public int getAnzahl() {
        return anzahl;
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
    public int compareTo(final Suchergebnis o) {
        /* TODO Comparable */return 0;
    }

    @Override
    public String toString() {
        return String.format("Suchergebnis{suchparameter=%s, titelnummern=%d, anzahl=%d}",
                suchparameter, titelnummern.size(), anzahl);
    }

}
