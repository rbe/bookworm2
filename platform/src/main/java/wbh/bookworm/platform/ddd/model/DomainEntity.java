/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

import java.io.Serializable;
import java.util.Objects;

public abstract class DomainEntity<T extends Serializable & Comparable<T>>
        implements Serializable, Comparable<T> {

    protected DomainId<String> domainId;

    protected DomainEntity() {
    }

    protected DomainEntity(final DomainId<String> domainId) {
        Objects.requireNonNull(domainId, "DomainId needs to be set");
        this.domainId = new DomainId<>(domainId);
    }

    @SuppressWarnings({"unchecked"})
    public <T extends DomainId<String>> T getDomainId() {
        Objects.requireNonNull(domainId, "DomainId needs to be set");
        return (T) domainId;
    }

}
