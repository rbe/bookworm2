/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.crypto.pgp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public final class ArmoredKeyPair implements Serializable {

    private static final long serialVersionUID = -1L;

    private final ArmoredKey privateKey;

    private final ArmoredKey publicKey;

    @JsonCreator
    private ArmoredKeyPair(@JsonProperty("privateKey") ArmoredKey privateKey,
                           @JsonProperty("publicKey") ArmoredKey publicKey) {
        Objects.requireNonNull(privateKey);
        Objects.requireNonNull(publicKey);
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public ArmoredKey privateKey() {
        return privateKey;
    }

    public ArmoredKey publicKey() {
        return publicKey;
    }

    public static ArmoredKeyPair of(ArmoredKey privateKey, ArmoredKey publicKey) {
        return new ArmoredKeyPair(privateKey, publicKey);
    }

    @Override
    public String toString() {
        return String.format("ArmoredKeyPair{privateKey=%s, publicKey=%s}",
                privateKey, publicKey);
    }

}
