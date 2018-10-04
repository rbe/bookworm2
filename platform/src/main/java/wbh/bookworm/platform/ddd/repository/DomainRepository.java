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
import java.util.function.Consumer;

@DomainRespositoryComponent
@SuppressWarnings({"squid:S00119"})
public interface DomainRepository
        <T extends DomainAggregate<T, ID>, ID extends DomainId<String>> {

    ID nextId();

    ID nextId(String prefix);

    default ID save(T aggregate) {
        throw new UnsupportedOperationException();
    }

    default Set<ID> saveAll(Set<T> aggregates) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> load(ID domainId) {
        return Optional.empty();
    }

    default <SUBT extends T> Optional<SUBT> load(ID domainId, final Class<SUBT> klass) {
        return Optional.empty();
    }

    default Optional<Set<T>> loadAll() {
        return Optional.empty();
    }

    default boolean withTransaction(ID domainId, Consumer<T> body) {
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
