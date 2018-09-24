/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.WildcardQuery;

import java.util.Arrays;

public class BooleanQueryBuilder {

    private final BooleanQuery.Builder builder;

    public BooleanQueryBuilder() {
        builder = new BooleanQuery.Builder();
    }

    public BooleanQueryBuilder add(final QueryParameters.Field field,
                                   final String value) {
        final boolean hasValue = null != value && !value.trim().isEmpty();
        if (hasValue) {
            final String fieldName = field.getName().toLowerCase();
            final String[] split = value.split("[ ]");
            Arrays.stream(split).forEach(v -> {
                final String wildcardSearchTerm = String.format("*%s*", v.toLowerCase());
                final WildcardQuery wildcardQuery = new WildcardQuery(new Term(fieldName, wildcardSearchTerm));
                builder.add(wildcardQuery, BooleanClause.Occur.valueOf(field.getOccur().name()));
            });
        }
        return this;
    }

    BooleanQuery build() {
        return builder.build();
    }

}
