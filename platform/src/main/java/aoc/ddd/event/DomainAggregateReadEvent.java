/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.event;

import aoc.ddd.model.DomainAggregate;
import aoc.ddd.model.DomainId;

@SuppressWarnings({"squid:S00119"})
public class DomainAggregateReadEvent
        <AGG extends DomainAggregate<AGG, ID>, ID extends DomainId<String>>
        extends DomainEvent {

    private final DomainAggregate<AGG, ID> domainAggregate;

    public DomainAggregateReadEvent(final DomainAggregate<AGG, ID> domainAggregate) {
        this.domainAggregate = domainAggregate;
    }

    public DomainAggregate<AGG, ID> getDomainAggregate() {
        return domainAggregate;
    }

}
