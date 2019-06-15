/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.crypto.pgp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;

public final class ArmoredKey implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String key;

    @JsonCreator
    public ArmoredKey(String key) {
        Objects.requireNonNull(key);
        this.key = key;
    }

    @JsonValue
    public String key() {
        return key;
    }

    public byte[] key(final Charset charset) {
        return key.getBytes(charset);
    }

    @Override
    public String toString() {
        return String.format("ArmoredKey{key=length %d}", key.length());
    }

}
