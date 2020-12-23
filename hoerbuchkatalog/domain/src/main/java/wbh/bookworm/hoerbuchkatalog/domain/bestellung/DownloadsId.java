/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import aoc.mikrokosmos.ddd.model.DomainId;

public class DownloadsId extends DomainId<String> {

    @JsonCreator
    public DownloadsId(final @JsonProperty("value") String value) {
        super(value);
    }

}
