/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Entity
 */
public final class Suchparameter implements Serializable {

    public enum Feld {

        STICHWORT("Stichwort"),
        SACHGEBIET("Sachgebiet"),
        AUTOR("Autor"),
        TITEL("Titel"),
        ERLAEUTERUNG("Erläuterung"),
        SUCHWOERTER("Suchwörter"),
        UNTERTITEL("Untertitel"),
        SPRECHER("Sprecher"),
        EINSTELLDATUM("Einstelldatum");

        private final String label;

        Feld(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

    }

    private Map<Feld, String> felderMitWerten = new EnumMap<>(Feld.class);

    public void hinzufuegen(final Feld feld, final String value) {
        this.felderMitWerten.put(feld, value);
    }

    public String wert(final Feld feld) {
        return felderMitWerten.get(feld);
    }

    public Map<Feld, String> getFelderMitWerten() {
        return felderMitWerten;
    }

    /*
    private String stichwort;

    private String sachgebiet;

    private String autor;

    private String titel;

    private String sprecher;

    private String einstelldatum;

    public String getStichwort() {
        return stichwort;
    }

    public void setStichwort(final String stichwort) {
        this.stichwort = stichwort;
    }

    public Sachgebiet[] getSachgebiete() {
        return Sachgebiet.values();
    }

    public String getSachgebiet() {
        return sachgebiet;
    }

    public void setSachgebiet(final String sachgebiet) {
        this.sachgebiet = sachgebiet;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(final String autor) {
        this.autor = autor;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getSprecher() {
        return sprecher;
    }

    public void setSprecher(final String sprecher) {
        this.sprecher = sprecher;
    }

    public String getEinstelldatum() {
        return einstelldatum;
    }

    public void setEinstelldatum(final String einstelldatum) {
        this.einstelldatum = einstelldatum;
    }
    */

    private StringBuilder appendIfSet(final StringBuilder builder, final String prefix, final String value) {
        Objects.requireNonNull(builder);
        final boolean hasPrefix = null != prefix && !prefix.trim().isEmpty();
        final boolean hasValue = null != value && !value.trim().isEmpty();
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

    public String getLabel() {
        final StringBuilder builder = new StringBuilder();
        felderMitWerten.forEach((feld, wert) -> appendIfSet(builder, feld.label, wert));
        return builder.toString();
    }

    @Override
    public String toString() {
        return String.format("Suchparameter{%s" +
                "}", getLabel());
    }

    public static void main(String[] args) {
        Suchparameter sp = new Suchparameter();
        sp.hinzufuegen(Feld.SACHGEBIET, "Z");
        sp.hinzufuegen(Feld.TITEL, "Das Kapital");
        sp.hinzufuegen(Feld.TITEL, "Das Kapital - II");
        System.out.println(sp.getLabel());
    }

}
