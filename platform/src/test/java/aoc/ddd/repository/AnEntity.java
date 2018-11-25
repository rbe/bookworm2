/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import aoc.ddd.model.DomainEntity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

class AnEntity extends DomainEntity<AnEntity, AnEntityId> {

    private static final long serialVersionUID = -1L;

    @JsonProperty
    private String bla;

    @JsonCreator
    AnEntity(final @JsonProperty("domainId") AnEntityId domainId,
             final @JsonProperty("bla") String bla) {
        super(domainId);
        this.bla = bla;
    }

    String getBla() {
        return bla;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AnEntity anEntity = (AnEntity) o;
        return Objects.equals(bla, anEntity.bla);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bla);
    }

    @Override
    public int compareTo(final AnEntity o) {
        /* TODO Comparable */
        return 0;
    }

    @Override
    public String toString() {
        return "AnEntity{" +
                "bla='" + bla + '\'' +
                ", domainId=" + domainId +
                '}';
    }

}
