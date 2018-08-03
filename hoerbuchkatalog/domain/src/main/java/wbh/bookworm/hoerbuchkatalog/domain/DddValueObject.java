/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class DddValueObject<T extends Comparable<T>, V extends Comparable<V>>
        implements Serializable, Comparable<T> {

    protected static final long serialVersionUID = -1L;

    protected V value;

    boolean checkValue(V value) {
        Objects.requireNonNull(value);
        return true;
    }

    public V getValue() {
        return value;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DddValueObject<T, V> other = (DddValueObject<T, V>) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public int compareTo(final T other) {
        final DddValueObject<T, V> cast = (DddValueObject<T, V>) other;
        return cast.value.compareTo(this.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
