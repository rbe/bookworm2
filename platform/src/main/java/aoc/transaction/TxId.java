/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class TxId implements Serializable, Comparable<TxId> {

    private static final long serialVersionUID = -1L;

    private final String value;

    public TxId() {
        this.value = UUID.randomUUID().toString();
    }

    @JsonCreator
    public TxId(String txId) {
        Objects.requireNonNull(txId);
        this.value = UUID.fromString(txId).toString();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TxId txId = (TxId) o;
        return value.equals(txId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(final TxId other) {
        return other.value.compareTo(this.value);
    }

    @Override
    public String toString() {
        return String.format("TxId{%s}", value);
    }

}
