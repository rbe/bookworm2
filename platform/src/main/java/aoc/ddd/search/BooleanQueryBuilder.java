/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.WildcardQuery;

import java.util.Arrays;

public class BooleanQueryBuilder {

    private final BooleanQuery.Builder builder;

    public BooleanQueryBuilder() {
        builder = new BooleanQuery.Builder();
    }

    public BooleanQueryBuilder addExactPhrase(final QueryParameters.Field field,
                                              final /* TODO Suchparameter anpassen oder SearchTerm o.ä. einführen, siehe regex */String value) {
        final boolean hasValue = null != value && !value.isBlank();
        if (hasValue) {
            final String fieldName = field.getName().toLowerCase();
            final String searchTerm = value
                    .replaceAll("[^A-Za-z0-9?!]", "")
                    .replace("?", "\\?")
                    .replace("!", "\\!");
            final PhraseQuery query = new PhraseQuery(fieldName, searchTerm);
            builder.add(query, BooleanClause.Occur.valueOf(field.getOccur().name()));
        }
        return this;
    }

    /*
    public BooleanQueryBuilder addRange(final QueryParameters.Field field,
                                        LocalDate from) {
        final boolean hasValue = null != value && !value.isBlank();
        if (hasValue) {
            final String fieldName = field.getName().toLowerCase();
            final String searchTerm = value
                    .replaceAll("[^A-Za-z0-9?!]", "")
                    .replace("?", "\\?")
                    .replace("!", "\\!");
            final PhraseQuery query = new PhraseQuery(fieldName, searchTerm);
            builder.add(query, BooleanClause.Occur.valueOf(field.getOccur().name()));
        }
        return this;
    }
    */

    public BooleanQueryBuilder addLowercaseWildcard(final QueryParameters.Field field,
                                                    final /* TODO Suchparameter anpassen oder SearchTerm o.ä. einführen, siehe regex */String value) {
        final boolean hasValue = null != value && !value.isBlank();
        if (hasValue) {
            final String fieldName = field.getName().toLowerCase();
            final String[] split = value.split("[ ,-/]");
            Arrays.stream(split).forEach(v -> {
                final String s = v.toLowerCase()
                        .replaceAll("[^A-Za-z0-9?!]", "")
                        .replace("?", "\\?")
                        .replace("!", "\\!");
                final String wildcardSearchTerm = String.format("*%s*", s);
                final WildcardQuery query = new WildcardQuery(new Term(fieldName, wildcardSearchTerm));
                builder.add(query, BooleanClause.Occur.valueOf(field.getOccur().name()));
            });
        }
        return this;
    }

    BooleanQuery build() {
        return builder.build();
    }

}
