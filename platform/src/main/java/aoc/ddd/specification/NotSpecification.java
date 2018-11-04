/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.specification;

public final class NotSpecification<T> implements Specification<T> {

    private final Specification<T> spec;

    public NotSpecification(final Specification<T> spec) {
        this.spec = spec;
    }

    @Override
    public boolean isSatisfied(final T candidate) {
        return !spec.isSatisfied(candidate);
    }

}
