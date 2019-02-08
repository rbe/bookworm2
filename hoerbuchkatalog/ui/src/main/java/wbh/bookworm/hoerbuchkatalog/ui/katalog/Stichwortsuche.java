/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

// TODO Mit Katalogsuchergebnis kombinieren: Seiten paginieren
public final class Stichwortsuche<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Stichwortsuche.class);

    private final List<T> liste;

    private String stichwort;

    private List<T> gefiltert;

    public Stichwortsuche(final List<T> liste) {
        this.liste = liste;
        gefiltert = Collections.emptyList();
    }

    public String getStichwort() {
        return stichwort;
    }

    public void setStichwort(final String stichwort) {
        this.stichwort = stichwort;
        if (!isStichwortEingegeben()) {
            gefiltert = Collections.emptyList();
        }
    }

    public boolean isStichwortEingegeben() {
        return null != stichwort && !stichwort.isBlank();
    }

    public void sucheNachStichwort(final BiPredicate<T, String> predicate) {
        LOGGER.trace("stichwort={}", stichwort);
        if (isStichwortEingegeben()) {
            gefiltert = liste.stream()
                    .filter(elt -> predicate.test(elt, stichwort))
                    .collect(Collectors.toList());
        } else {
            gefiltert = Collections.emptyList();
        }
        LOGGER.trace("gefiltert={}", gefiltert);
    }

    public boolean isStichwortHatTreffer() {
        return isStichwortEingegeben() && !gefiltert.isEmpty();
    }

    public List<T> getGefiltert() {
        return gefiltert;
    }

    public void stichwortVergessen() {
        stichwort = null;
        gefiltert = Collections.emptyList();
    }

}
