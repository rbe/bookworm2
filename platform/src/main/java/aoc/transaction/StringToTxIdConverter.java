/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.transaction;

import org.springframework.core.convert.converter.Converter;

public final class StringToTxIdConverter implements Converter<String, TxId> {

    @Override
    public TxId convert(final String source) {
        return new TxId(source);
    }

}
