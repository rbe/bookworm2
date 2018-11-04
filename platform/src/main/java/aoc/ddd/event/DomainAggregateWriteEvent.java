/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.event;

import aoc.ddd.model.DomainAggregate;
import aoc.ddd.model.DomainId;

import java.util.Objects;

@SuppressWarnings({"squid:S00119"})
public class DomainAggregateWriteEvent
        <AGG extends DomainAggregate<AGG, ID>, ID extends DomainId<String>>
        extends DomainEvent {

    private final DomainAggregate<AGG, ID> domainAggregate;

    public DomainAggregateWriteEvent(final DomainAggregate<AGG, ID> domainAggregate) {
        this.domainAggregate = domainAggregate;
    }

    public ID getDomainId() {
        return domainAggregate.getDomainId();
    }

    public DomainAggregate<AGG, ID> getDomainAggregate() {
        return domainAggregate;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        if (!super.equals(other)) return false;
        final DomainAggregateWriteEvent<?, ?> that = (DomainAggregateWriteEvent<?, ?>) other;
        return Objects.equals(domainAggregate, that.domainAggregate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), domainAggregate);
    }

}
