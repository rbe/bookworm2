/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

@SuppressWarnings({"squid:S00119"})
public abstract class DomainAggregate
        <T extends DomainAggregate<T, ID>, ID extends DomainId<String>>
        extends DomainEntity<T, ID> {

    protected DomainAggregate() {
    }

    protected DomainAggregate(final ID domainId) {
        super(domainId);
    }

}
