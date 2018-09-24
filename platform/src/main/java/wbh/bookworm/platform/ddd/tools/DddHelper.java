/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.tools;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class DddHelper {

    public static List<String> propertiesToValues(final Object object,
                                                  final Set<String> fields) {
        return fields.stream()
                .map(s -> getFieldValueAsString(object, s))
                .collect(Collectors.toList());
    }

    public static String valueAsString(final Object object, final String fieldName) {
        return getFieldValueAsString(object, fieldName);
    }

    public static boolean getPropertyValueAsStringIfContains(final Object object,
                                                             final String fieldName,
                                                             final String term) {
        final String value = getFieldValueAsString(object, fieldName);
        return value.contains(term);
    }

    private static String getFieldValueAsString(final Object object,
                                                final String fieldName) {
        try {
            final Field field = getField(object, fieldName);
            return field.get(object).toString();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new DomainClassException(e);
        }
    }

    private static Field getField(final Object object,
                                  final String fieldName) throws NoSuchFieldException {
        final Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
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
