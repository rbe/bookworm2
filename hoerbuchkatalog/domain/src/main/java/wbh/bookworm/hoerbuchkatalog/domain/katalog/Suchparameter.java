/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import aoc.ddd.model.DomainValueObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Value Object
 * TODO Suchparameter anpassen oder SearchTerm o.ä. einführen, siehe regex
 */
public final class Suchparameter extends DomainValueObject {

    private static final long serialVersionUID = -1L;

    public enum Feld {

        STICHWORT("Stichwort", "stichwort"),
        SACHGEBIET("Sachgebiet", "sachgebiet"),
        AUTOR("Autor", "autor"),
        TITEL("Titel", "titel"),
        ERLAEUTERUNG("Erläuterung", "erlaeuterung"),
        SUCHWOERTER("Suchwörter", "suchwoerter"),
        UNTERTITEL("Untertitel", "untertitel"),
        SPRECHER("Sprecher", "sprecher"),
        SPRECHER1("Sprecher1", "sprecher1"),
        SPRECHER2("Sprecher2", "sprecher2"),
        EINSTELLDATUM("Einstelldatum", "einstelldatum");

        private final String label;

        private final String luceneName;

        Feld(final String label, final String luceneName) {
            this.label = label;
            this.luceneName = luceneName;
        }

        public String getLabel() {
            return label;
        }

        public String luceneName() {
            return luceneName;
        }

    }

    private Map<Feld, String> felderMitWerten = new EnumMap<>(Feld.class);

    public Suchparameter() {
    }

    public Suchparameter(Suchparameter suchparameter) {
        this.felderMitWerten.putAll(suchparameter.felderMitWerten);
    }

    public Suchparameter hinzufuegen(final Feld feld, final String value) {
        this.felderMitWerten.put(feld, value);
        return this;
    }

    public Suchparameter entfernen(final Feld feld) {
        this.felderMitWerten.remove(feld);
        return this;
    }

    public String wert(final Feld feld) {
        return felderMitWerten.getOrDefault(feld, "");
    }

    public Map<Feld, String> getFelderMitWerten() {
        return felderMitWerten;
    }

    public Feld[] getFeldnamen() {
        return felderMitWerten.keySet().toArray(Suchparameter.Feld[]::new);
    }

    public Feld[] getFeldnamenMitWerten() {
        return felderMitWerten.entrySet().stream()
                .filter(e->null!=e.getValue()&&!e.getValue().isBlank())
                .map(Map.Entry::getKey)
                .toArray(Feld[]::new);
    }

    private StringBuilder appendIfSet(final StringBuilder builder, final String prefix, final String value) {
        Objects.requireNonNull(builder);
        final boolean hasPrefix = null != prefix && !prefix.isBlank();
        final boolean hasValue = null != value && !value.isBlank();
        if (hasValue) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            if (hasPrefix) {
                builder.append(prefix).append(" ");
            }
            builder.append("\"").append(value).append("\"");
        }
        return builder;
    }

    public String alsText() {
        final StringBuilder builder = new StringBuilder();
        felderMitWerten.forEach((feld, wert) -> appendIfSet(builder, feld.label, wert));
        return builder.toString();
    }

    public boolean isWerteVorhanden() {
        return !felderMitWerten.isEmpty();
    }

    public void leeren() {
        felderMitWerten.clear();
    }

    @Override
    public String toString() {
        return String.format("Suchparameter{%s" +
                "}", alsText());
    }

}
