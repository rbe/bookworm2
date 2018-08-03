/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Aggregate
 */
public final class Hoerbuchkatalog extends DddAggregate<Map<Titelnummer, Hoerbuch>> {

    private final Map<Titelnummer, Hoerbuch> katalog;

    public Hoerbuchkatalog() {
        this.katalog = new ConcurrentHashMap<>();
    }

    public Hoerbuchkatalog(final Map<Titelnummer, Hoerbuch> katalog) {
        this.katalog = katalog;
    }

    public void hinzufuegen(final Hoerbuch hoerbuch) {
        katalog.put(hoerbuch.getTitelnummer(), hoerbuch);
    }

    public int anzahlHoerbuecher() {
        return katalog.size();
    }

    public int anzahlDownloadbarerHoerbuecher() {
        return katalog.values().stream()
                .filter(Hoerbuch::isDownloadbar)
                .collect(Collectors.toUnmodifiableList())
                .size();
    }

    public Set<Hoerbuch> alleHoerbuecher() {
        final TreeSet<Hoerbuch> treeSet = new TreeSet<>(Hoerbuch::compareTo);
        treeSet.addAll(katalog.values());
        return treeSet;
    }

    public Hoerbuch hoerbuch(final Titelnummer titelnummer) {
        return katalog.get(titelnummer);
    }

    @Override
    public int compareTo(final Map<Titelnummer, Hoerbuch> o) {
        return 0;
    }

}
