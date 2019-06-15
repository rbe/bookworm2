/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.transaction;

import java.io.Serializable;

public final class TxResult implements Serializable {

    private static final long serialVersionUID = -1L;

    private final int code;

    private final String message;

    public TxResult(final int code, final String message) {
        this.code = code;
        this.message = null != message ? message : "";
    }

    public static TxResult success() {
        return new TxResult(200, null);
    }

    public static TxResult unknownTxId(TxId txId) {
        return new TxResult(400, "Unknown transaction " + txId.getValue());
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("TxResult{code=%d, message='%s'}", code, message);
    }

}
