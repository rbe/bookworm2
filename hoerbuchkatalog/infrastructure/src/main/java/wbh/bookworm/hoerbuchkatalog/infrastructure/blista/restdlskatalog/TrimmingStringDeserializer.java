/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;

final class TrimmingStringDeserializer extends FromStringDeserializer<String> {

    private static final long serialVersionUID = 1L;

    protected TrimmingStringDeserializer() {
        super(null);
    }

    @Override
    protected String _deserialize(final String value, final DeserializationContext ctxt) {
        return value;
    }

}
