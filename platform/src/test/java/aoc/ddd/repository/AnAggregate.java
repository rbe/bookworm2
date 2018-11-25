/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import aoc.ddd.model.DomainAggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

class AnAggregate extends DomainAggregate<AnAggregate, AnAggregateId> {

    private static final long serialVersionUID = -1L;

    @JsonProperty
    AnEntity anEntity;

    @JsonCreator
    AnAggregate(final @JsonProperty("domainId") AnAggregateId domainId,
                final @JsonProperty("anEntity") AnEntity anEntity) {
        super(domainId);
        this.anEntity = anEntity;
    }

    AnEntity getAnEntity() {
        return anEntity;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AnAggregate that = (AnAggregate) o;
        return Objects.equals(anEntity, that.anEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainId, anEntity);
    }

    @Override
    public int compareTo(final AnAggregate o) {
        /* TODO Comparable */
        return 0;
    }

    @Override
    public String toString() {
        return String.format("AnAggregate{domainId=%s, anEntity=%s}", domainId, anEntity);
    }

}
