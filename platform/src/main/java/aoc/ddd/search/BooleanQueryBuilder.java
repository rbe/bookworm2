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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class BooleanQueryBuilder {

    private static final String NOT_ALLOWED_CHARACTERS = "^[\\\\u0000-\\\\u007F]*$";

    private final BooleanQuery.Builder builder;

    public BooleanQueryBuilder() {
        builder = new BooleanQuery.Builder();
    }

    public BooleanQueryBuilder addTerm(final QueryParameters.Field field,
                                       final /* TODO Suchparameter anpassen oder SearchTerm o.ä. einführen, siehe regex */String value) {
        final boolean hasValue = null != value && !value.isBlank();
        if (hasValue) {
            final String fieldName = field.getName().toLowerCase();
            final String searchTerm = value
                    .replaceAll(NOT_ALLOWED_CHARACTERS, "")
                    .replace("?", "\\?")
                    .replace("!", "\\!");
            final Term term = new Term(fieldName, searchTerm);
            final TermQuery query = new TermQuery(term);
            builder.add(query, BooleanClause.Occur.valueOf(field.getOccur().name()));
        }
        return this;
    }

    public BooleanQueryBuilder addExactPhrase(final QueryParameters.Field field,
                                              final /* TODO Suchparameter anpassen oder SearchTerm o.ä. einführen, siehe regex */String value) {
        final boolean hasValue = null != value && !value.isBlank();
        if (hasValue) {
            final String fieldName = field.getName().toLowerCase();
            final String searchTerm = value
                    .replaceAll(NOT_ALLOWED_CHARACTERS, "")
                    .replace("?", "\\?")
                    .replace("!", "\\!");
            final PhraseQuery query = new PhraseQuery(fieldName, searchTerm);
            builder.add(query, BooleanClause.Occur.valueOf(field.getOccur().name()));
        }
        return this;
    }

    public BooleanQueryBuilder addLowercaseWildcard(final QueryParameters.Field field,
                                                    final /* TODO Suchparameter anpassen oder SearchTerm o.ä. einführen, siehe regex */String value) {
        final boolean hasValue = null != value && !value.isBlank();
        if (hasValue) {
            final String fieldName = field.getName().toLowerCase();
            final String[] split = value.split("[ ,-/]");
            Arrays.stream(split).forEach(v -> {
                final String s = v.toLowerCase()
                        .replaceAll(NOT_ALLOWED_CHARACTERS, "")
                        .replace("?", "\\?")
                        .replace("!", "\\!");
                final String wildcardSearchTerm = String.format("*%s*", s);
                final WildcardQuery query = new WildcardQuery(new Term(fieldName, wildcardSearchTerm));
                builder.add(query, BooleanClause.Occur.valueOf(field.getOccur().name()));
            });
        }
        return this;
    }

    public BooleanQueryBuilder addRange(final QueryParameters.Field field, LocalDate from, LocalDate to) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(from);
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        final String fieldName = field.getName().toLowerCase();
        final TermRangeQuery query = new TermRangeQuery(fieldName,
                new BytesRef(from.format(dtf).getBytes()), null,
                true, true);
        builder.add(query, BooleanClause.Occur.valueOf(field.getOccur().name()));
        return this;
    }

    public void add(final Query query, final BooleanClause.Occur occur) {
        builder.add(query, occur);
    }

    public BooleanQuery build() {
        return builder.build();
    }

    @Override
    public String toString() {
        return build().toString();
    }

}
