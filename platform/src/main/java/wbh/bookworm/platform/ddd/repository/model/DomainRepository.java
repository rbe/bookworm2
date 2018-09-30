/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.model;

import wbh.bookworm.platform.ddd.model.DomainAggregate;
import wbh.bookworm.platform.ddd.model.DomainId;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@DomainRespositoryComponent
@SuppressWarnings({"squid:S00119"})
public interface DomainRepository
        <T extends DomainAggregate<T, ID>, ID extends DomainId<String>> {

    ID nextId();

    default ID save(final T aggregate) {
        throw new UnsupportedOperationException();
    }

    default Set<ID> saveAll(final Set<T> aggregates) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> load(final ID domainId) {
        return Optional.empty();
    }

    default <SUBT extends T> Optional<SUBT> load(final ID domainId, final Class<SUBT> klass) {
        return Optional.empty();
    }

    default Optional<Set<T>> loadAll() {
        return Optional.empty();
    }

    default boolean withTransaction(final ID domainId, final Consumer<T> body) {
        final Optional<T> aggregate = load(domainId);
        if (aggregate.isPresent()) {
            body.accept(aggregate.get());
            save(aggregate.get());
            return true;
        } else {
            throw new DomainRepositoryException();
        }
    }

    default long countAll() {
        return -1;
    }

}
