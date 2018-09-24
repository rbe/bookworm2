/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

import java.io.Serializable;
import java.util.UUID;

public class DomainId<T extends Serializable & Comparable<T>> extends ValueObject<T, String> {

    private static final long serialVersionUID = -1L;

    protected DomainId() {
    }

    public DomainId(final String value) {
        super(value);
    }

    public DomainId(final DomainId<T> domainId) {
        this(domainId.value);
    }

    public DomainId(final Integer dddId) {
        this(String.valueOf(dddId));
    }

    public DomainId(final Long dddId) {
        this(String.valueOf(dddId));
    }

    @Override
    public boolean checkValue(final String value) {
        /* TODO ID must be filesystem compatible: no space, slash, ... */
        return super.checkValue(value);
    }

    public static DomainId createDefault() {
        return new DomainId(UUID.randomUUID().toString());
    }

}
