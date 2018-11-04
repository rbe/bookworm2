/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.specification;

import java.util.function.Predicate;

public interface Specification<T> {

    boolean isSatisfied(T candidate);

    default Predicate<T> toPredicate() {
        return this::isSatisfied;
    }

    default Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    default Specification<T> andNot(Specification<T> other) {
        return new AndNotSpecification<>(this, other);
    }

    default Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    default Specification<T> orNot(Specification<T> other) {
        return new OrNotSpecification<>(this, other);
    }

    default Specification<T> not() {
        return new NotSpecification<>(this);
    }

}

