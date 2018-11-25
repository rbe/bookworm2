/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import aoc.ddd.model.DomainId;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class AnEntityId extends DomainId<String> {

    @JsonCreator
    AnEntityId(final @JsonProperty("value") String value) {
        super(value);
    }

}
