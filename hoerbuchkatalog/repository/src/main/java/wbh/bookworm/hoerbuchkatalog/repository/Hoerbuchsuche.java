/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;

import java.lang.reflect.Field;

@Component
final class Hoerbuchsuche {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hoerbuchsuche.class);

    boolean findeHoerbuecherMitTermInFeld(final Hoerbuch hoerbuch, final String fieldName, final String term) {
        try {
            final Field field = hoerbuch.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            final String value = (String) field.get(hoerbuch);
            final boolean contains = value.contains(term);
            if (contains) {
                LOGGER.trace("'{}' in '{}' gefunden", term, value);
            } else {
                LOGGER.trace("'{}' in '{}' nicht gefunden", term, value);
            }
            return contains;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("", e);
            return false;
        }
    }

    /*
    Set<Titelnummer> suchen(final Map<String, String[]> felderUndTerme) {
        Set<Hoerbuch> suchergebnis = new TreeSet<>(Hoerbuch::compareTo);
        katalog.forEach(((titelnummer, hoerbuch) -> {
            felderUndTerme.forEach((feld, terme) -> {
                Arrays.stream(terme).forEach(term -> {
                    final boolean found = hoerbuchSuche.findeHoerbuecherMitTermInFeld(hoerbuch, feld, term);
                    if (found) {
                        suchergebnis.add(hoerbuch);
                        LOGGER.debug("Suchwort '{}' in Hörbuch {}, Feld {} gefunden", term, titelnummer, feld);
                    }
                });
            });
        }));
        return suchergebnis;
    }
    */

}
