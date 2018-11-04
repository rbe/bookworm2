/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.specification;

public abstract class CompositeSpecification<T> implements Specification<T> {

    @Override
    public Specification<T> and(final Specification<T> other) {
        return null;
    }

    @Override
    public Specification<T> andNot(final Specification<T> other) {
        return null;
    }

    @Override
    public Specification<T> or(final Specification<T> other) {
        return null;
    }

    @Override
    public Specification<T> orNot(final Specification<T> other) {
        return null;
    }

    @Override
    public Specification<T> not() {
        return null;
    }

}
