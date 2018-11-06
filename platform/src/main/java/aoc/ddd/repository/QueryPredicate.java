/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

public abstract class QueryPredicate {

    protected final String field;

    protected final String value;

    private QueryPredicate(final String field, final String value) {
        this.field = field;
        this.value = value;
    }

    public static final class Equals extends QueryPredicate {

        private Equals(final String field, final String value) {
            super(field, value);
        }

        public static Equals of(final String field, final String value) {
            return new Equals(field, value);
        }

        @Override
        public boolean isSatisfied(final Object other) {
            return other.toString().equals(value);
        }

    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public abstract boolean isSatisfied(final Object other);

}
