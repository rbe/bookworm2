/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.HoerbuchkatalogId;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.shared.domain.hoerbuch.AghNummer;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainAggregate;

// TODO ...ist eine Kombination: Set<Hoerbuch> mit LuceneIndex
// TODO Daten geändert->zB JSF Beans: a) Version nutzen b) Event schicken
public final class Hoerbuchkatalog extends DomainAggregate<Hoerbuchkatalog, HoerbuchkatalogId> {

    private final transient Map<Titelnummer, Hoerbuch> katalog;

    private transient HoerbuchkatalogSuche hoerbuchkatalogSuche;

    public Hoerbuchkatalog(final HoerbuchkatalogId hoerbuchkatalogId,
                           final long version) {
        super(hoerbuchkatalogId);
        this.katalog = new HashMap<>();
        this.version.set(version);
    }

    public static Hoerbuchkatalog leererHoerbuchkatalog() {
        return new Hoerbuchkatalog(new HoerbuchkatalogId("Leer.dat"), 0);
    }

    void setHoerbuchkatalogSuche(final HoerbuchkatalogSuche hoerbuchkatalogSuche) {
        this.hoerbuchkatalogSuche = hoerbuchkatalogSuche;
    }

    void hinzufuegen(final Hoerbuch hoerbuch) {
        katalog.put(hoerbuch.getTitelnummer(), hoerbuch);
    }

    public int anzahlHoerbuecherGesamt() {
        return katalog.size();
    }

    public int anzahlDownloadbarerHoerbuecher() {
        return katalog.values().stream()
                .filter(Hoerbuch::isDownloadbar)
                .collect(Collectors.toUnmodifiableList())
                .size();
    }

    public Set<Hoerbuch> alle() {
        final TreeSet<Hoerbuch> treeSet = new TreeSet<>(Hoerbuch::compareTo);
        treeSet.addAll(katalog.values());
        return treeSet;
    }

    public boolean enthaelt(final Titelnummer titelnummer) {
        return katalog.containsKey(titelnummer);
    }

    public boolean hoerbuchDownloadbar(final Titelnummer titelnummer) {
        return katalog.get(titelnummer).isDownloadbar();
    }

    /**
     * Holt das Hörbuch zur Titelnummer.
     * Gibt ein unbekanntes Hörbuch zurück, wenn die Titelnummer nicht im Katalog ist.
     * @return {@link Hoerbuch}.
     */
    public Hoerbuch hole(final Titelnummer titelnummer) {
        final Hoerbuch hoerbuch = katalog.get(titelnummer);
        return null != hoerbuch ? hoerbuch : Hoerbuch.unbekannt(titelnummer);
    }

    public List<Hoerbuch> hole(final Titelnummer... titelnummern) {
        return Arrays.stream(titelnummern)
                .map(this::hole)
                .collect(Collectors.toUnmodifiableList());
    }

    public Optional<Hoerbuch> hole(final AghNummer aghNummer) {
        return katalog.values().stream()
                .filter(e -> e.hatAghNummer(aghNummer))
                .findFirst();
    }

    public AghNummer[] titelnummerZuAghNummer(final Collection<Titelnummer> titelnummern) {
        return titelnummern.stream()
                .map(tn -> hole(tn).getAghNummer())
                .toArray(AghNummer[]::new);
    }

    public Suchergebnis suchen(final Suchparameter suchparameter) {
        return hoerbuchkatalogSuche.suchen(suchparameter);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Hoerbuchkatalog that = (Hoerbuchkatalog) o;
        return Objects.equals(katalog, that.katalog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(katalog);
    }

    @Override
    public int compareTo(final Hoerbuchkatalog other) {
        /* TODO Comparable */
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Hoerbuchkatalog{domainId=%s, version=%d, groesse=%s}",
                domainId, version.get(), katalog.size());
    }

}
