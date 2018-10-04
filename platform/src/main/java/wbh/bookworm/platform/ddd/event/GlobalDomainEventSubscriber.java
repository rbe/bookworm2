/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.event;

public abstract class GlobalDomainEventSubscriber<T extends DomainEvent>
        extends DomainEventSubscriber<T> {

    protected GlobalDomainEventSubscriber(final Class<T> domainEvent) {
        super(domainEvent);
    }

}
