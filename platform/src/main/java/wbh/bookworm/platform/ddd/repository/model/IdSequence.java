/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

final class IdSequence implements Serializable {

    private static final long serialVersionUID = -1L;

    private final AtomicLong counter;

    IdSequence() {
        this(0L);
    }

    IdSequence(final long counter) {
        this.counter = new AtomicLong(counter);
    }

    @JsonValue
    long getCounter() {
        return counter.get();
    }

    long incrementAndGet() {
        return counter.incrementAndGet();
    }

    String incrementAndGetAsHex() {
        return String.format("x%08x", incrementAndGet());
    }

    @Override
    public String toString() {
        return String.format("IdSequence{counter=%s}", counter);
    }

/*
    public static void main(String[] args) {
        final IdSequence idSequence = new IdSequence(10);
        //             2_500_011
        for (int i=0;i<2_500_000;i++) System.out.println(idSequence.incrementAndGetAsHex());
        System.out.println(idSequence.incrementAndGet());
    }
*/

}
