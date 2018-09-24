/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository;

import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainId;

import java.util.Optional;
import java.util.Set;

@DomainRespositoryComponent
public interface DomainRepository<T extends DomainAggregate<?>> {

    default void saveAll(final Set<T> aggregates) {
        throw new UnsupportedOperationException();
    }

    default void save(final T aggregate) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> load(final DomainId<?> domainId) {
        return Optional.empty();
    }

    default <R extends T> Optional<R> load(final DomainId<?> domainId, final Class<R> klass) {
        return Optional.empty();
    }

    default Optional<Set<T>> loadAll() {
        return Optional.empty();
    }

}
