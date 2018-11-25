/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.email;

import aoc.ddd.model.DomainId;

public final class EmailTemplateId extends DomainId<String> {

    public EmailTemplateId(final String value) {
        super(value);
    }

}
