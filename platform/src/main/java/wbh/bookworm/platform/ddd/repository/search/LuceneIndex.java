/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.search;

import wbh.bookworm.platform.ddd.tools.DddHelper;
import wbh.bookworm.platform.ddd.model.DomainEntity;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static wbh.bookworm.platform.ddd.repository.search.Constants.DDD_ID;

public class LuceneIndex {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneIndex.class);

    private Analyzer analyzer;

    private Directory directory;

    private IndexWriter indexWriter;

    private IndexReader indexReader;

    public LuceneIndex(final Path path) throws IOException {
        this.analyzer = new StandardAnalyzer();
        this.directory = new MMapDirectory(path);
    }

    private IndexWriterConfig getIndexWriterConfig() {
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCommitOnClose(true);
        return config;
    }

    public boolean deleteIndex() {
        try {
            if (null != indexReader) {
                indexReader.close();
            }
            indexWriter = new IndexWriter(directory, getIndexWriterConfig());
            LOGGER.debug("Lösche {} Einträge aus dem Index {}", indexWriter.numDocs(), directory);
            indexWriter.deleteAll();
            indexWriter.commit();
            return true;
        } catch (IOException e) {
            LOGGER.error("Could not delete index", e);
            return false;
        }
    }

    private void add(final Document document) throws IOException {
        LOGGER.trace("Füge {} zum Suchindex hinzu", document);
        indexWriter.addDocument(document);
    }

    public <T extends DomainEntity<?>> boolean add(final T dddEntity,
                                                   final String dddIdField,
                                                   final String[] stringFields,
                                                   final String[] textFields,
                                                   final String[] dateFields,
                                                   final String[] sortFields) {
        Objects.requireNonNull(dddEntity);
        LOGGER.trace("Füge {} zum Suchindex hinzu", dddEntity);
        Document document = new Document();
        document.add(new StringField(DDD_ID,
                DddHelper.valueAsString(dddEntity, dddIdField),
                Field.Store.YES));
        Arrays.stream(stringFields)
                .forEach(f -> document.add(new StringField(f,
                        DddHelper.valueAsString(dddEntity, f),
                        Field.Store.NO)));
        Arrays.stream(textFields)
                .forEach(f -> document.add(new TextField(f,
                        DddHelper.valueAsString(dddEntity, f),
                        Field.Store.NO)));
        Arrays.stream(dateFields)
                .forEach(f -> {
                    final String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    document.add(new StringField(f, date, Field.Store.NO));
                });
        Arrays.stream(sortFields)
                .forEach(f -> document.add(new SortedDocValuesField(f,
                        new BytesRef(DddHelper.valueAsString(dddEntity, f)))));
        try {
            add(document);
            return true;
        } catch (IOException e) {
            LOGGER.error("", e);
            return false;
        }
    }

    public <T extends DomainEntity<?>> boolean add(final Set<T> dddEntity,
                                                   final String dddIdField,
                                                   final String[] stringFields,
                                                   final String[] textFields,
                                                   final String[] dateFields,
                                                   final String[] sortFields) {
        Objects.requireNonNull(dddEntity);
        if (dddEntity.isEmpty()) {
            LOGGER.warn("Keine Dokumente zum Indizieren vorhanden");
            return false;
        } else {
            LOGGER.debug("Füge {} Dokumente zum Suchindex hinzu", dddEntity.size());
            for (final T dddAggregate : dddEntity) {
                if (!add(dddAggregate, dddIdField, stringFields, textFields, dateFields, sortFields)) {
                    return false;
                }
            }
            return true;
        }
    }

    public void build() {
        Objects.requireNonNull(indexWriter);
        if (indexWriter.maxDoc() > 0) {
            try {
                final int maxDoc = indexWriter.maxDoc();
                indexWriter.close();
                indexReader = DirectoryReader.open(directory);
                LOGGER.info("Suchindex erfolgreich aufgebaut, {} Einträge vorhanden", maxDoc);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException("Keine Dokumente im Index");
        }
    }

    IndexReader getIndexReader() {
        Objects.requireNonNull(indexReader, "Please call build() after add()ing documents to the index");
        return indexReader;
    }

}
