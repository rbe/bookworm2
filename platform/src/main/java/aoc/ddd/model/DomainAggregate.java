/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.model;

import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"squid:S00119"})
public abstract class DomainAggregate
        <AGG extends DomainAggregate<AGG, ID>, ID extends DomainId<String>>
        extends DomainEntity<AGG, ID> {

    protected final AtomicLong version;

    protected DomainAggregate(final ID domainId) {
        super(domainId);
        this.version = new AtomicLong(1);
    }

    protected DomainAggregate(final ID domainId, long version) {
        super(domainId);
        this.version = new AtomicLong(version);
    }

    public long getVersion() {
        return version.get();
    }

    public long incVersion() {
        return version.incrementAndGet();
    }

}
