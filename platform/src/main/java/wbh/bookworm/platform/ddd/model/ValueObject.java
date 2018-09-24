/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @param <T>
 * @param <V>
 */
public abstract class ValueObject<T extends Serializable & Comparable<T>,
        V extends Serializable & Comparable<V>> implements Serializable, Comparable<T> {

    protected static final long serialVersionUID = -1L;

    protected V value;

    protected ValueObject() {
    }

    public ValueObject(final V value) {
        Objects.requireNonNull(value);
        if (!checkValue(value)) {
            throw new IllegalArgumentException(String.format("'%s'", value));
        }
        this.value = value;
    }

    public boolean checkValue(V value) {
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
        final ValueObject<T, V> other = (ValueObject<T, V>) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public int compareTo(final T other) {
        final ValueObject<T, V> cast = (ValueObject<T, V>) other;
        return cast.value.compareTo(this.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
