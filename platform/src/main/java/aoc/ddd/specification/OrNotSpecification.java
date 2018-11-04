/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.specification;

public final class OrNotSpecification<T> implements Specification<T> {

    private final Specification<T> left;

    private final Specification<T> right;

    public OrNotSpecification(final Specification<T> left, final Specification<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfied(final T candidate) {
        return left.isSatisfied(candidate) || !right.isSatisfied(candidate);
    }

}
