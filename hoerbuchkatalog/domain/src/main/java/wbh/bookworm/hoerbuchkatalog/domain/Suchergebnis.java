/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Entity
 */
public final class Suchergebnis implements Serializable {

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
        return titelnummern.indexOf(titelnummer) > 0;
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

}
