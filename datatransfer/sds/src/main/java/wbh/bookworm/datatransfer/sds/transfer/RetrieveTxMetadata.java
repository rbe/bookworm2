/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.sds.transfer;

import aoc.mikrokosmos.incubation.transaction.TxId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class RetrieveTxMetadata implements Serializable {

    private static final long serialVersionUID = -1L;

    private final LocalDateTime created;

    private final TxId txId;

    private final String publicKey;

    private final String filename;

    public RetrieveTxMetadata(final String publicKey, final String filename) {
        Objects.requireNonNull(publicKey);
        created = LocalDateTime.now();
        this.txId = new TxId();
        this.publicKey = publicKey;
        this.filename = filename;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public TxId getTxId() {
        return txId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return String.format("StoreTxMetadata{%s, publicKey='%s'}",
                txId, publicKey.substring(1, 32));
    }

}
