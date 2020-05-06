/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import java.util.Collections;
import java.util.List;

import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

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
        this.titelnummern = Collections.unmodifiableList(titelnummern);
        anzahl = titelnummern.size();
        this.gesamtAnzahlTreffer = gesamtAnzahlTreffer;
    }

    public static Suchergebnis leeresSuchergebnis(final Suchparameter suchparameter) {
        return new Suchergebnis(suchparameter, Collections.emptyList(), 0);
    }

    public static Suchergebnis leeresSuchergebnis(final String stichwort) {
        final Suchparameter suchparameter =
                new Suchparameter().hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        return new Suchergebnis(suchparameter, Collections.emptyList(), 0);
    }

    public static Suchergebnis leeresSuchergebnis() {
        final Suchparameter suchparameter = new Suchparameter();
        return new Suchergebnis(suchparameter, Collections.emptyList(), 0);
    }

    public Suchparameter getSuchparameter() {
        return suchparameter;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public boolean hatErgebnisse() {
        return anzahl > 0;
    }

    public int getGesamtAnzahlTreffer() {
        return gesamtAnzahlTreffer;
    }

    public List<Titelnummer> getTitelnummern() {
        return titelnummern;
    }

    @SuppressWarnings({"squid:S2692", "java:S2692"})
    public boolean vorherigeVorhanden(final Titelnummer titelnummer) {
        return titelnummern.indexOf(titelnummer) > 0;
    }

    public Titelnummer vorherige(final Titelnummer titelnummer) {
        int prevIdx = titelnummern.indexOf(titelnummer) - 1;
        return prevIdx > -1 ? titelnummern.get(prevIdx) : null;
    }

    public boolean naechsteVorhanden(final Titelnummer titelnummer) {
        return titelnummern.indexOf(titelnummer) < titelnummern.size() - 1;
    }

    public Titelnummer naechste(final Titelnummer titelnummer) {
        int nextIdx = titelnummern.indexOf(titelnummer) + 1;
        return nextIdx <= titelnummern.size() ? titelnummern.get(nextIdx) : null;
    }

    @Override
    public String toString() {
        return String.format("Suchergebnis{suchparameter=%s, titelnummern=%d, anzahl=%d, gesamtAnzahlTreffer=%d}",
                suchparameter, titelnummern.size(), anzahl, gesamtAnzahlTreffer);
    }

}
