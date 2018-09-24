/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainId;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * TODO ...ist eine Kombination: Set<Hoerbuch> mit LuceneIndex
 */
public final class Hoerbuchkatalog extends DomainAggregate<Hoerbuchkatalog> {

    private final Map<Titelnummer, Hoerbuch> katalog;

    private HoerbuchkatalogSuche hoerbuchkatalogSuche;

    public Hoerbuchkatalog(final DomainId<String> gesamtDat,
                           final Map<Titelnummer, Hoerbuch> katalog) {
        super(gesamtDat);
        this.katalog = katalog;
    }

    void setHoerbuchkatalogSuche(final HoerbuchkatalogSuche hoerbuchkatalogSuche) {
        this.hoerbuchkatalogSuche = hoerbuchkatalogSuche;
    }

    public void hinzufuegen(final Hoerbuch hoerbuch) {
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

    public Hoerbuch finde(final Titelnummer titelnummer) {
        return katalog.get(titelnummer);
    }

    public Suchergebnis sucheNachStichwort(final String stichwort) {
        return hoerbuchkatalogSuche.sucheNachStichwort(stichwort);
    }

    public Suchergebnis suchen(final Suchparameter suchparameter) {
        return hoerbuchkatalogSuche.suchen(suchparameter);
    }

    @Override
    public int compareTo(final Hoerbuchkatalog o) {
        /* TODO Comparable */return 0;
    }

}
