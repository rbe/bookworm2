/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import wbh.bookworm.platform.ddd.model.DomainId;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DomainId / Value Object
 */
public final class Titelnummer extends DomainId<String> {

    private static final long serialVersionUID = -1L;

    @JsonCreator
    public Titelnummer(final @JsonProperty("value") String value) {
        super(value);
    }

    public Titelnummer(final Integer value) {
        super(value);
    }

    public Titelnummer(final Titelnummer titelnummer) {
        this(titelnummer.value);
    }

}
