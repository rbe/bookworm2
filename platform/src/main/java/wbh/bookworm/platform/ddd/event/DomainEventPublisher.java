/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public final class DomainEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventPublisher.class);

    @SuppressWarnings("unchecked")
    private final List<GlobalDomainEventSubscriber> subscribers;

    private final AtomicReference<Boolean> publishing;

    private static final class Factory {

        private static final DomainEventPublisher GLOBAL = new DomainEventPublisher();

        private static final Map<String, DomainEventPublisher> instances = new ConcurrentHashMap<>();

        private static DomainEventPublisher global() {
            return GLOBAL;
        }

        private static DomainEventPublisher instance(final String discriminator) {
            return instances.computeIfAbsent(discriminator, k -> new DomainEventPublisher());
        }

    }

    public static DomainEventPublisher global() {
        return Factory.global();
    }

    public static DomainEventPublisher instance(final String discriminator) {
        return Factory.instance(discriminator);
    }

    private DomainEventPublisher() {
        LOGGER.trace("Initialising {}", this);
        subscribers = new CopyOnWriteArrayList<>();
        publishing = new AtomicReference<>(Boolean.FALSE);
        LOGGER.debug("Initialised {}", this);
    }

    public DomainEventPublisher reset() {
        LOGGER.trace("Try to reset {}", this);
        if (!publishing.get()) {
            subscribers.clear();
            LOGGER.debug("Not publishing, resetted subscribers for {}", this);
        }
        return this;
    }

    @SuppressWarnings({"unchecked"})
    public void publish(final DomainEvent domainEvent) {
        LOGGER.trace("Try to publish {} on {}", domainEvent, this);
        if (publishing.get()) {
            LOGGER.warn("{} is currently publishing, did not publish {}", this, domainEvent);
            return;
        }
        try {
            publishing.set(Boolean.TRUE);
            LOGGER.trace("{} is publishing {}", this, domainEvent);
            if (null != subscribers) {
                final Class<?> eventType = domainEvent.getClass();
                for (final GlobalDomainEventSubscriber subscriber : subscribers) {
                    LOGGER.trace("{} is publishing {} to {}", this, domainEvent, subscriber);
                    final Class<?> subscribedTo = subscriber.subscribedToEventType();
                    if (subscribedTo == eventType) {
                        subscriber.handleEvent(domainEvent);
                        LOGGER.debug("{} published {} to {}", this, domainEvent, subscriber);
                    } else if (subscribedTo == DomainEvent.class) {
                        subscriber.handleEvent(domainEvent);
                        LOGGER.debug("{} published {} to {} as it's interested in DomainEventS",
                                this, domainEvent, subscriber);
                    }
                }
            } else {
                LOGGER.warn("No subscribers to {} on {} found", domainEvent, this);
            }
        } finally {
            publishing.set(Boolean.FALSE);
            LOGGER.trace("{} finished publishing {}", this, domainEvent);
        }
    }

    @SuppressWarnings("unchecked")
    public void subscribe(final GlobalDomainEventSubscriber subscriber) {
        LOGGER.trace("Trying to subscribe {} to {}", subscriber, this);
        if (publishing.get()) {
            LOGGER.trace("{} is currently publishing, won't subscribe {}", this, subscriber);
            return;
        }
        if (subscribers.add(subscriber)) {
            LOGGER.debug("Successfully subscribed {} to {}", subscriber, this);
        }
    }

}
