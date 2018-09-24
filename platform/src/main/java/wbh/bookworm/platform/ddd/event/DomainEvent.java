/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class DomainEvent implements Serializable, Comparable<DomainEvent> {

    private static final long serialVersionUID = -1L;

    private final LocalDateTime occuredOn;

    protected DomainEvent() {
        this.occuredOn = LocalDateTime.now();
    }

    public LocalDateTime occuredOn() {
        return LocalDateTime.of(occuredOn.toLocalDate(), occuredOn.toLocalTime());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DomainEvent that = (DomainEvent) o;
        return Objects.equals(occuredOn, that.occuredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(occuredOn);
    }

    @Override
    public int compareTo(final DomainEvent o) {
        /* TODO Comparable */return this.getClass().getName().compareTo(o.getClass().getName()) * occuredOn.compareTo(o.occuredOn);
    }

}
