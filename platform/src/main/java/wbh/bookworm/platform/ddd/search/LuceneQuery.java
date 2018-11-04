/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.search;

import wbh.bookworm.platform.ddd.model.DomainId;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static wbh.bookworm.platform.ddd.search.Constants.DOMAIN_ID;

public final class LuceneQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneQuery.class);

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    @SuppressWarnings({"squid:S1452"})
    public static List<DomainId<?>> query(final LuceneIndex luceneIndex,
                                          final BooleanQueryBuilder queryBuilder,
                                          final String... sortFields) {
        Objects.requireNonNull(queryBuilder);
        Objects.requireNonNull(luceneIndex);
        Objects.requireNonNull(luceneIndex.getIndexReader());
        final long startNanos = System.nanoTime();
        final IndexSearcher searcher = new IndexSearcher(luceneIndex.getIndexReader());
        try {
            final SortField[] sortFieldStream = Arrays.stream(sortFields)
                    .map(f -> new SortField(f.toLowerCase(), SortField.Type.STRING_VAL))
                    .toArray(SortField[]::new);
            final Sort sort = new Sort(sortFieldStream);
            final TopDocs topDocs = searcher.search(queryBuilder.build(), Integer.MAX_VALUE, sort);
            final long nanos = System.nanoTime() - startNanos;
            LOGGER.debug("Query took {} ns (= {} ms = {} s)",
                    decimalFormat.format(nanos),
                    decimalFormat.format(nanos / 1_000_000),
                    decimalFormat.format(nanos / 1_000_000 / 1_000));
            return topDocsToDomainIds(searcher, topDocs);
        } catch (IOException e) {
            LOGGER.error("Cannot execute query", e);
            return Collections.emptyList();
        }
    }

    private static List<DomainId<?>> topDocsToDomainIds(final IndexSearcher searcher,
                                                        final TopDocs topDocs) {
        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        final Document doc = searcher.doc(scoreDoc.doc);
                        LOGGER.trace("Document#{} DomainId#{} im Index gefunden",
                                scoreDoc.doc, doc.get(DOMAIN_ID));
                        return new DomainId<>(doc.get(DOMAIN_ID));
                    } catch (IOException e) {
                        LOGGER.error("", e);
                        /* TODO Function may return null, but it's not allowed here */return null;
                    }
                })
                .collect(Collectors.toUnmodifiableList());
    }

}
