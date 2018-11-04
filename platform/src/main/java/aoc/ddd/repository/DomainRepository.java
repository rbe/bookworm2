/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import aoc.ddd.event.DomainAggregateWriteEvent;
import aoc.ddd.event.DomainEvent;
import aoc.ddd.event.DomainEventPublisher;
import aoc.ddd.event.DomainEventSubscriber;
import aoc.ddd.model.DomainAggregate;
import aoc.ddd.model.DomainId;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@DomainRespositoryComponent
@SuppressWarnings({"squid:S00119"})
public interface DomainRepository
        <AGG extends DomainAggregate<AGG, ID>, ID extends DomainId<String>> {

    ID nextIdentity();

    ID nextIdentity(String prefix);

    AGG create();

    default AGG save(AGG aggregate) {
        throw new UnsupportedOperationException();
    }

    default Set<AGG> saveAll(Set<AGG> aggregates) {
        throw new UnsupportedOperationException();
    }

    default Optional<AGG> load(ID domainId) {
        return Optional.empty();
    }

    default <SUBT extends AGG> Optional<SUBT> load(ID domainId, final Class<SUBT> klass) {
        return Optional.empty();
    }

    default Optional<Set<AGG>> loadAll() {
        return Optional.empty();
    }

    default Optional<Set<AGG>> find(Predicate... predicates) {
        return Optional.empty();
    }

    /**
     * Transaction with optimistic locking.
     */
    default AGG withTransaction(AGG aggregate, Consumer<AGG> body) {
        final Optional<AGG> loadedAggregate = load(aggregate.getDomainId());
        if (loadedAggregate.isPresent()) {
            if (loadedAggregate.get().getVersion() == aggregate.getVersion()) {
                body.accept(aggregate);
                return save(aggregate);
            } else {
                throw new DomainRepositoryException();
            }
        } else {
            throw new DomainRepositoryException();
        }
    }

    default long countAll() {
        return loadAll().orElseThrow(DomainRepositoryException::new).size();
    }

    @SuppressWarnings({"unchecked"})
    default <E extends DomainEvent> void registerEventHandler(Logger logger, Class<E> domainEvent, Consumer<E> consumer) {
        Objects.requireNonNull(logger);
        Objects.requireNonNull(domainEvent);
        Objects.requireNonNull(consumer);
        final DomainEventSubscriber<E> subscriber = new DomainEventSubscriber<>(domainEvent) {

            @Override
            public void handleEvent(final E domainEvent) {
                logger.trace("{} handling received event {} with {}", this, domainEvent, consumer);
                consumer.accept(domainEvent);
                logger.debug("{} handled received event {} with {}", this, domainEvent, consumer);
            }

            @Override
            public String toString() {
                return "registerEventHandler{" + domainEvent + ", consumer=" + consumer + "}";
            }

        };
        DomainEventPublisher.global().subscribe(subscriber);
    }

    @SuppressWarnings({"unchecked"})
    default <E extends DomainAggregateWriteEvent<AGG, ID>> void saveOnEvent(Logger logger, Class<E> domainEvent) {
        Objects.requireNonNull(logger);
        Objects.requireNonNull(domainEvent);
        DomainEventPublisher.global().subscribe(new DomainEventSubscriber<>(domainEvent) {

            @Override
            public void handleEvent(final E domainEvent) {
                logger.trace("{}/saveOnEvent handling received event: {}", this, domainEvent);
                save((AGG) domainEvent.getDomainAggregate());
                logger.debug("{}/saveOnEvent handled received event: {}", this, domainEvent);
            }

            @Override
            public String toString() {
                return "saveOnEvent(" + domainEvent + ')';
            }

        });
    }

}
