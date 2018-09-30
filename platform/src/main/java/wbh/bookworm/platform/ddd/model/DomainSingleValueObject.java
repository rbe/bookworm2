/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings({"squid:S00119"})
public abstract class DomainSingleValueObject
        <TYPE extends Serializable & Comparable<TYPE>, VALUE extends Serializable & Comparable<VALUE>>
        implements Serializable, Comparable<TYPE> {

    private static final long serialVersionUID = -1L;

    protected VALUE value;

    protected DomainSingleValueObject() {
    }

    public DomainSingleValueObject(final VALUE value) {
        Objects.requireNonNull(value);
        if (!checkValue(value)) {
            throw new IllegalArgumentException(String.format("'%s'", value));
        }
        this.value = value;
    }

    public boolean checkValue(VALUE value) {
        Objects.requireNonNull(value);
        return true;
    }

    public VALUE getValue() {
        return value;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DomainSingleValueObject<TYPE, VALUE> other = (DomainSingleValueObject<TYPE, VALUE>) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public int compareTo(final TYPE other) {
        final DomainSingleValueObject<TYPE, VALUE> cast = (DomainSingleValueObject<TYPE, VALUE>) other;
        return cast.value.compareTo(this.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
