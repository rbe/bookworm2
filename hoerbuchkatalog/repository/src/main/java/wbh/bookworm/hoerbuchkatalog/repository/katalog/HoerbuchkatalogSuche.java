/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.ddd.model.DomainId;
import aoc.ddd.search.BooleanQueryBuilder;
import aoc.ddd.search.LuceneIndex;
import aoc.ddd.search.LuceneQuery;
import aoc.ddd.search.QueryParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class HoerbuchkatalogSuche {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogSuche.class);

    private final LuceneIndex luceneIndex;

    private final int anzahlSuchergebnisse;

    HoerbuchkatalogSuche(final ApplicationContext applicationContext,
                         final DomainId<String> hoerbuchkatalogDomainId,
                         final int anzahlSuchergebnisse) {
        this.luceneIndex = applicationContext.getBean(
                LuceneIndex.class, hoerbuchkatalogDomainId.getValue());
        this.anzahlSuchergebnisse = anzahlSuchergebnisse;
    }

    void indiziere(final Set<Hoerbuch> hoerbuecher) {
        LOGGER.trace("Indiziere {} Hörbücher", hoerbuecher.size());
        if (luceneIndex.hasIndex()) {
            LOGGER.info("Hörbuchkatalog mit {} Einträgen wurde bereits indiziert", hoerbuecher.size());
        } else {
            luceneIndex.deleteIndex()
                    .add(hoerbuecher,
                            "titelnummer",
                            new String[]{Suchparameter.Feld.SACHGEBIET.luceneName()},
                            new String[]{
                                    Suchparameter.Feld.AUTOR.luceneName(),
                                    Suchparameter.Feld.TITEL.luceneName(),
                                    Suchparameter.Feld.UNTERTITEL.luceneName(),
                                    Suchparameter.Feld.ERLAEUTERUNG.luceneName(),
                                    Suchparameter.Feld.SPRECHER1.luceneName(),
                                    Suchparameter.Feld.SUCHWOERTER.luceneName()
                            },
                            new String[]{Suchparameter.Feld.EINSTELLDATUM.luceneName()},
                            new String[]{
                                    Suchparameter.Feld.AUTOR.luceneName(),
                                    Suchparameter.Feld.TITEL.luceneName()
                            })
                    .build();
            LOGGER.info("Hörbuchkatalog mit {} Einträgen indiziert", hoerbuecher.size());
        }
    }

    Suchergebnis sucheNachStichwort(final String stichwort) {
        // TODO final String stichwort = suchparameter.wert(Feld.STICHWORT);
        LOGGER.trace("Suche nach Stichwort '{}'", stichwort);
        final BooleanQueryBuilder booleanQueryBuilder = new BooleanQueryBuilder()
                .add(new QueryParameters.Field(Suchparameter.Feld.AUTOR.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.TITEL.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.UNTERTITEL.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.ERLAEUTERUNG.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.SUCHWOERTER.name(), QueryParameters.Occur.SHOULD), stichwort);
        final LuceneQuery.Result result = LuceneQuery.query(this.luceneIndex, booleanQueryBuilder, anzahlSuchergebnisse,
                Suchparameter.Feld.AUTOR.name(), Suchparameter.Feld.TITEL.name());
        final List<Titelnummer> titelnummern = result.getDomainIds()
                        .stream()
                        .map(dddId -> new Titelnummer(dddId.getValue()))
                        .collect(Collectors.toUnmodifiableList());
        final Suchparameter suchparameter =
                new Suchparameter().hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        final Suchergebnis suchergebnis = new Suchergebnis(
                suchparameter, titelnummern, result.getTotalMatchingCount());
        LOGGER.debug("Suche nach Stichwort '{}' ergab {}", stichwort, suchergebnis);
        return suchergebnis;
    }

    Suchergebnis suchen(final Suchparameter suchparameter) {
        LOGGER.info("Suche nach '{}'", suchparameter);
        final BooleanQueryBuilder booleanQueryBuilder = new BooleanQueryBuilder()
                .add(new QueryParameters.Field(Suchparameter.Feld.SACHGEBIET.name(), QueryParameters.Occur.SHOULD), suchparameter.wert(Suchparameter.Feld.SACHGEBIET));
        suchparameter.getFelderMitWerten().keySet()
                .forEach(k -> booleanQueryBuilder.add(new QueryParameters.Field(k.name(), QueryParameters.Occur.MUST), suchparameter.wert(k)));
        final LuceneQuery.Result result = LuceneQuery.query(this.luceneIndex, booleanQueryBuilder, 1000,
                Suchparameter.Feld.AUTOR.name(), Suchparameter.Feld.TITEL.name());
        final List<Titelnummer> titelnummern = result.getDomainIds()
                .stream()
                .map(dddId -> new Titelnummer(dddId.getValue()))
                .collect(Collectors.toList());
        return new Suchergebnis(suchparameter, titelnummern, result.getTotalMatchingCount());
    }

}
