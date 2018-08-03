/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.domain.Titelnummer;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public final class HoerbuchkatalogSuche {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogSuche.class);

    private final Path luceneIndexDirectory;

    private final Analyzer analyzer;

    private final Directory directory;

    private IndexWriter indexWriter;

    private IndexReader indexReader;

    @Autowired
    HoerbuchkatalogSuche(final RepositoryConfig repositoryConfig) throws IOException {
        analyzer = new StandardAnalyzer();
        luceneIndexDirectory = repositoryConfig.getLuceneIndexDirectory();
        directory = new MMapDirectory(luceneIndexDirectory);
    }

    void indexAufbauen(final Set<Hoerbuch> hoerbuecher) throws IOException {
        LOGGER.debug("Baue Suchindex auf");
        for (final Hoerbuch hoerbuch : hoerbuecher) {
            hoerbuchHinzufuegen(hoerbuch);
        }
        LOGGER.info("Suchindex erfolgreich aufgebaut, {} Einträge vorhanden", indexWriter.maxDoc());
        indexWriter.close();
        indexReader = DirectoryReader.open(directory);
    }

    void indexLoeschen() throws IOException {
        if (null != indexReader) {
            indexReader.close();
        }
        indexWriter = new IndexWriter(directory, getIndexWriterConfig());
        LOGGER.debug("Lösche {} Einträge aus dem Index", indexWriter.numDocs());
        indexWriter.deleteAll();
        indexWriter.commit();
    }

    private void hoerbuchHinzufuegen(final Hoerbuch hoerbuch) throws IOException {
        Document document = new Document();
        document.add(new StringField("titelnummer", hoerbuch.getTitelnummer().getValue(), Field.Store.YES));
        document.add(new StringField("sachgebiet", hoerbuch.getSachgebiet().name(), Field.Store.NO));
        document.add(new TextField("autor", hoerbuch.getAutor(), Field.Store.NO));
        document.add(new SortedDocValuesField("autor", new BytesRef(hoerbuch.getAutor())));
        document.add(new TextField("titel", hoerbuch.getTitel(), Field.Store.NO));
        document.add(new SortedDocValuesField("titel", new BytesRef(hoerbuch.getTitel())));
        document.add(new TextField("untertitel", hoerbuch.getUntertitel(), Field.Store.NO));
        document.add(new TextField("erlaeuterung", hoerbuch.getErlaeuterung(), Field.Store.NO));
        document.add(new TextField("sprecher1", hoerbuch.getSprecher1(), Field.Store.NO));
        document.add(new TextField("suchwoerter", hoerbuch.getSuchwoerter(), Field.Store.NO));
        final String einstelldatum = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        document.add(new StringField("einstelldatum", einstelldatum, Field.Store.NO));
        indexWriter.addDocument(document);
    }

    private void addValueToBooleanQuery(final BooleanQuery.Builder builder, BooleanClause.Occur occur,
                                        final String wert, final Suchparameter.Feld feld) {
        final boolean wertGesetzt = null != wert && !wert.trim().isEmpty();
        if (wertGesetzt) {
            final String fld = feld.name().toLowerCase();
            final String text = String.format("*%s*", wert.toLowerCase());
            builder.add(new WildcardQuery(new Term(fld, text)), occur);
        }
    }

    private void addSuchparameterToBooleanQuery(final BooleanQuery.Builder builder, BooleanClause.Occur occur,
                                                final Suchparameter suchparameter, final Suchparameter.Feld feld) {
        final String wert = suchparameter.wert(feld);
        addValueToBooleanQuery(builder, occur, wert, feld);
    }

    public Suchergebnis sucheNachStichwort(final Suchparameter suchparameter) {
        final String value = suchparameter.wert(Suchparameter.Feld.STICHWORT);
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        addValueToBooleanQuery(builder, BooleanClause.Occur.SHOULD, value, Suchparameter.Feld.AUTOR);
        addValueToBooleanQuery(builder, BooleanClause.Occur.SHOULD, value, Suchparameter.Feld.TITEL);
        addValueToBooleanQuery(builder, BooleanClause.Occur.SHOULD, value, Suchparameter.Feld.UNTERTITEL);
        addValueToBooleanQuery(builder, BooleanClause.Occur.SHOULD, value, Suchparameter.Feld.ERLAEUTERUNG);
        addValueToBooleanQuery(builder, BooleanClause.Occur.SHOULD, value, Suchparameter.Feld.SUCHWOERTER);
        final BooleanQuery booleanQuery = builder.build();
        return new Suchergebnis(suchparameter, performQuery(booleanQuery));
    }

    public Suchergebnis suchen(final Suchparameter suchparameter) {
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        addSuchparameterToBooleanQuery(builder, BooleanClause.Occur.SHOULD,
                suchparameter, Suchparameter.Feld.SACHGEBIET);
        suchparameter.getFelderMitWerten().keySet()
                .forEach(key -> addSuchparameterToBooleanQuery(builder, BooleanClause.Occur.MUST,
                        suchparameter, key));
        final BooleanQuery booleanQuery = builder.build();
        return new Suchergebnis(suchparameter, performQuery(booleanQuery));
    }

    private List<Titelnummer> performQuery(final Query query) {
        IndexSearcher searcher = new IndexSearcher(indexReader);
        try {
            final Sort sort = new Sort(
                    new SortField("autor", SortField.Type.STRING_VAL),
                    new SortField("titel", SortField.Type.STRING_VAL)
            );
            final TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE, sort);
            return getTitelnummern(searcher, topDocs);
        } catch (IOException e) {
            LOGGER.error("Suche nicht durchführbar", e);
            return Collections.emptyList();
        }
    }

    private LinkedList<Titelnummer> getTitelnummern(final IndexSearcher searcher, final TopDocs topDocs) {
        return Arrays.stream(topDocs.scoreDocs)
                .map(sd -> {
                    try {
                        final Document doc = searcher.doc(sd.doc);
                        LOGGER.debug("Dokument#{} Titelnummer#{} im Index gefunden",
                                sd.doc, doc.get("titelnummer"));
                        return new Titelnummer(doc.get("titelnummer"));
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                    return null;
                })
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private IndexWriterConfig getIndexWriterConfig() {
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCommitOnClose(true);
        return config;
    }

}
