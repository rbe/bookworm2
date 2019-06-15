/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public final class DomainEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventPublisher.class);

    private final String name;

    private final Set<DomainEventSubscriber> subscribers;

    private final AtomicReference<Boolean> publishing;

    private static final class Factory {

        private static final DomainEventPublisher GLOBAL = new DomainEventPublisher("GLOBAL");

        private static final Map<String, DomainEventPublisher> INSTANCES = new ConcurrentHashMap<>();

        private static DomainEventPublisher global() {
            return GLOBAL;
        }

        private static DomainEventPublisher instance(final String discriminator) {
            return INSTANCES.computeIfAbsent(discriminator, DomainEventPublisher::new);
        }

    }

    public static DomainEventPublisher global() {
        return Factory.global();
    }

    public static DomainEventPublisher discriminator(final String discriminator) {
        return Factory.instance(discriminator);
    }

    private DomainEventPublisher(final String name) {
        LOGGER.trace("Initialising {}: {}", name, this);
        this.name = name;
        subscribers = new CopyOnWriteArraySet<>();
        publishing = new AtomicReference<>(Boolean.FALSE);
        LOGGER.debug("Initialised {}: {}", name, this);
    }

    public synchronized DomainEventPublisher reset() {
        LOGGER.trace("Try to reset {}", this);
        if (!publishing.get()) {
            subscribers.clear();
            LOGGER.debug("Not publishing, resetted subscribers for {}", this);
        } else {
            LOGGER.error("Cannot reset {}, is currently publishing", this);
        }
        return this;
    }

    public void publishSync(final DomainEvent domainEvent) {
        publish(domainEvent);
    }

    @Async
    public void publishAsync(final DomainEvent domainEvent) {
        publish(domainEvent);
    }

    private void publish(final DomainEvent domainEvent) {
        LOGGER.trace("Try to publish {} on {}", domainEvent, this);
        /*if (publishing.get()) {
            LOGGER.warn("{} is currently publishing, did not publish {}", this, domainEvent);
            return;
        }*/
        try {
            publishing.set(Boolean.TRUE);
            LOGGER.trace("{} is publishing {}", this, domainEvent);
            if (null != subscribers) {
                publishEvent(domainEvent);
            } else {
                LOGGER.warn("{} has no subscribers", this);
            }
        } finally {
            publishing.set(Boolean.FALSE);
            LOGGER.trace("{} finished publishing {}", this, domainEvent);
        }
    }

    @SuppressWarnings({"unchecked"})
    private void publishEvent(final DomainEvent domainEvent) {
        final Class<?> eventType = domainEvent.getClass();
        for (final DomainEventSubscriber subscriber : subscribers) {
            LOGGER.trace("{} is publishing {} to {}", this, domainEvent, subscriber);
            final Class<?> subscribedToEventType = subscriber.subscribedToEventType();
            if (subscribedToEventType == eventType) {
                subscriber.handleEvent(domainEvent);
                LOGGER.debug("{} published {} to {}", this, domainEvent, subscriber);
            } else if (subscribedToEventType == DomainAggregateWriteEvent.class
                    && domainEvent instanceof DomainAggregateWriteEvent) {
                subscriber.handleEvent(domainEvent);
                LOGGER.debug("{} published {} to {} as it's interested in DomainAggregateWriteEventS",
                        this, domainEvent, subscriber);
            } else if (subscribedToEventType == DomainEvent.class) {
                subscriber.handleEvent(domainEvent);
                LOGGER.debug("{} published {} to {} as it's interested in DomainEventS",
                        this, domainEvent, subscriber);
            } else {
                LOGGER.warn("No subscribers which are interested in {} found", domainEvent);
            }
        }
    }

    public void subscribe(final DomainEventSubscriber subscriber) {
        LOGGER.trace("Trying to subscribe {} to {}", subscriber, this);
        /*if (publishing.get()) {
            LOGGER.trace("{} is currently publishing, won't subscribe {}", this, subscriber);
            return;
        }*/
        if (subscribers.add(subscriber)) {
            LOGGER.debug("{} has successfully subscribed to {}", subscriber, this);
        } else {
            LOGGER.error("{} could not subscribe to {}", subscriber, this);
        }
    }

    @Override
    public String toString() {
        return String.format("DomainEventPublisher{name='%s', subscribers=%d, currently publishing=%s}",
                name, subscribers.size(), publishing.get());
    }

}
