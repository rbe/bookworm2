/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DomainEventSubscriber<T extends DomainEvent> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Class<T> domainEvent;

    protected DomainEventSubscriber(final Class<T> domainEvent) {
        this.domainEvent = domainEvent;
    }

    public Class<T> subscribedToEventType() {
        return domainEvent;
    }

    public abstract void handleEvent(final T domainEvent);

}
