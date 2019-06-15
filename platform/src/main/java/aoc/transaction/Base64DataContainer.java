/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public final class Base64DataContainer implements Serializable {

    private static final long serialVersionUID = -1L;

    private final TxId txId;

    private final String filename;

    private final byte[] base64Data;

    @JsonCreator
    public Base64DataContainer(@JsonProperty("txId") final TxId txId,
                               @JsonProperty("filename") final String filename,
                               @JsonProperty("base64Data") final byte[] base64Data) {
        this.txId = txId;
        this.filename = filename;
        this.base64Data = base64Data;
    }

    public TxId getTxId() {
        return txId;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getBase64Data() {
        return base64Data;
    }

    @Override
    public String toString() {
        return String.format("Base64DataContainer{%s, filename='%s', base64Data=length %d}",
                txId, filename, base64Data.length);
    }

}
