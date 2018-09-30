/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
//@JsonTypeName("DomainId")
public class DomainId
        <T extends Serializable & Comparable<T>>
        extends DomainSingleValueObject<T, String> {

    private static final long serialVersionUID = -1L;

    protected DomainId() {
    }

    @JsonCreator
    public DomainId(final @JsonProperty("value") String value) {
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

}
