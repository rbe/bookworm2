/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.search;

import java.util.LinkedList;

public class QueryParameters {

    public static class Field {

        private final String name;

        private final String label;

        private final Occur occur;

        public Field(final String name, final Occur occur) {
            this(name, null, occur);
        }

        public Field(final String name, final String label, final Occur occur) {
            this.name = name;
            this.label = label;
            this.occur = occur;
        }

        public String getName() {
            return name;
        }

        public String getLabel() {
            return label;
        }

        public Occur getOccur() {
            return occur;
        }

    }

    public enum Occur {
        FILTER,
        SHOULD,
        MUST,
        MUST_NOT
    }

    private LinkedList<Field> fields = new LinkedList<>();

    public QueryParameters add(final Field field) {
        fields.add(field);
        return this;
    }

    /*
    public String valueOf(final Field field) {
        return "";
    }
    */

}
