/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.sds.transfer;

import aoc.mikrokosmos.crypto.pgp.ArmoredKey;
import aoc.mikrokosmos.incubation.transaction.TxId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class StoreTxMetadata implements Serializable {

    private static final long serialVersionUID = -1L;

    private final LocalDateTime created;

    private final TxId txId;

    private final ArmoredKey publicKey;

    public StoreTxMetadata(final ArmoredKey publicKey) {
        Objects.requireNonNull(publicKey);
        created = LocalDateTime.now();
        this.txId = new TxId();
        this.publicKey = publicKey;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public TxId getTxId() {
        return txId;
    }

    public String getPublicKey() {
        return publicKey.key();
    }

    @Override
    public String toString() {
        return String.format("StoreTxMetadata{%s, publicKey='%s'}",
                txId, publicKey.key().substring(1, 32));
    }

}
