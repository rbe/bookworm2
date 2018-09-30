/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings({"squid:S00119"})
public abstract class DomainEntity
        <T extends DomainEntity/*Serializable & Comparable<T>*/, ID extends DomainId<String>>
        implements Serializable, Comparable<T> {

    @JsonSerialize(using = DomainIdJacksonSupport.DomainIdSerializer.class
            /* TODO , typing = JsonSerialize.Typing.STATIC*/)
    @JsonDeserialize(using = DomainIdJacksonSupport.DomainIdDeserializer.class)
    protected ID domainId;

    protected DomainEntity() {
    }

    protected DomainEntity(final ID domainId) {
        Objects.requireNonNull(domainId, "DomainId needs to be set");
        this.domainId = domainId;
    }

    protected DomainEntity(final T entity) {
        Objects.requireNonNull(entity);
        throw new UnsupportedOperationException("Copy constructor not implemented");
    }

    @SuppressWarnings({"unchecked"})
    public ID getDomainId() {
        Objects.requireNonNull(domainId, "DomainId needs to be set");
        return domainId;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(final Object other);

    @Override
    public abstract String toString();

    @Override
    public int compareTo(final T other) {
        return this.domainId.value.compareTo((String) other.domainId.value);
    }

}
