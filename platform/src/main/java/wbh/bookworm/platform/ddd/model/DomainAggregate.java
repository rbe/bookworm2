/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

public abstract class DomainAggregate<T extends DomainAggregate<T>> extends DomainEntity<T> {

    protected DomainAggregate() {
    }

    protected DomainAggregate(final DomainId<String> domainId) {
        super(domainId);
    }

}
