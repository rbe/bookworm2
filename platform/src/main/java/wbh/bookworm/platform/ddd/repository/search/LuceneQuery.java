/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.search;

import wbh.bookworm.platform.ddd.model.DomainId;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static wbh.bookworm.platform.ddd.repository.search.Constants.DDD_ID;

public final class LuceneQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneQuery.class);

    @SuppressWarnings({"squid:S1452"})
    public static List<DomainId<?>> query(final LuceneIndex luceneIndex,
                                          final BooleanQueryBuilder queryBuilder,
                                          final String... sortFields) {
        Objects.requireNonNull(queryBuilder);
        Objects.requireNonNull(luceneIndex);
        Objects.requireNonNull(luceneIndex.getIndexReader());
        final IndexSearcher searcher = new IndexSearcher(luceneIndex.getIndexReader());
        try {
            final SortField[] sortFieldStream = Arrays.stream(sortFields)
                    .map(f -> new SortField(f, SortField.Type.STRING_VAL))
                    .toArray(SortField[]::new);
            final Sort sort = new Sort(sortFieldStream);
            final TopDocs topDocs = searcher.search(queryBuilder.build(), Integer.MAX_VALUE, sort);
            return topDocsToDddIds(searcher, topDocs);
        } catch (IOException e) {
            LOGGER.error("Cannot execute query", e);
            return Collections.emptyList();
        }
    }

    private static List<DomainId<?>> topDocsToDddIds(final IndexSearcher searcher,
                                                     final TopDocs topDocs) {
        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        final Document doc = searcher.doc(scoreDoc.doc);
                        LOGGER.debug("Dokument#{} DomainId#{} im Index gefunden",
                                scoreDoc.doc, doc.get(DDD_ID));
                        return new DomainId<>(doc.get(DDD_ID));
                    } catch (IOException e) {
                        LOGGER.error("", e);
                        /* TODO Function may return null, but it's not allowed here */
                        return null;
                    }
                })
                .collect(Collectors.toUnmodifiableList());
    }

}
