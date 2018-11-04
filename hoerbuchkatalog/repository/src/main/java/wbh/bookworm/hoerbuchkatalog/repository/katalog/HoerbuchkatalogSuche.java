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

    HoerbuchkatalogSuche(final ApplicationContext applicationContext,
                         final DomainId<String> hoerbuchkatalogDomainId) {
        this.luceneIndex = applicationContext.getBean(LuceneIndex.class, hoerbuchkatalogDomainId.getValue());
    }

    void indiziere(final Set<Hoerbuch> hoerbuecher) {
        LOGGER.trace("Indiziere {} Hörbücher", hoerbuecher.size());
        luceneIndex.deleteIndex();
        luceneIndex.add(hoerbuecher,
                "titelnummer",
                new String[]{"sachgebiet"},
                new String[]{"autor", "titel", "untertitel", "erlaeuterung", "sprecher1", "suchwoerter"},
                new String[]{"einstelldatum"},
                new String[]{"autor", "titel"});
        luceneIndex.build();
        LOGGER.info("Hörbuchkatalog mit {} Einträgen indiziert", hoerbuecher.size());
    }

    Suchergebnis sucheNachStichwort(final String stichwort) {
        //final String stichwort = suchparameter.wert(Feld.STICHWORT);
        LOGGER.info("Suche nach Stichwort '{}'", stichwort);
        final BooleanQueryBuilder booleanQueryBuilder = new BooleanQueryBuilder()
                .add(new QueryParameters.Field(Suchparameter.Feld.AUTOR.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.TITEL.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.UNTERTITEL.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.ERLAEUTERUNG.name(), QueryParameters.Occur.SHOULD), stichwort)
                .add(new QueryParameters.Field(Suchparameter.Feld.SUCHWOERTER.name(), QueryParameters.Occur.SHOULD), stichwort);
        final List<Titelnummer> titelnummern =
                LuceneQuery.query(this.luceneIndex, booleanQueryBuilder,
                        Suchparameter.Feld.AUTOR.name(), Suchparameter.Feld.TITEL.name())
                        .stream()
                        .map(dddId -> new Titelnummer(dddId.getValue()))
                        .collect(Collectors.toUnmodifiableList());
/*
       TODO if (titelnummern.size() <= 1000) {
*/
            return new Suchergebnis(new Suchparameter().hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort),
                    titelnummern);
/*
        } else {
            return Optional.empty();
        }
*/
    }

    Suchergebnis suchen(final Suchparameter suchparameter) {
        LOGGER.info("Suche nach {}", suchparameter);
        final BooleanQueryBuilder booleanQueryBuilder = new BooleanQueryBuilder()
                .add(new QueryParameters.Field(Suchparameter.Feld.SACHGEBIET.name(), QueryParameters.Occur.SHOULD), suchparameter.wert(Suchparameter.Feld.SACHGEBIET));
        suchparameter.getFelderMitWerten().keySet()
                .forEach(k -> booleanQueryBuilder.add(new QueryParameters.Field(k.name(), QueryParameters.Occur.MUST), suchparameter.wert(k)));
        final List<DomainId<?>> result = LuceneQuery.query(this.luceneIndex, booleanQueryBuilder,
                Suchparameter.Feld.AUTOR.name(), Suchparameter.Feld.TITEL.name());
        return new Suchergebnis(suchparameter, result.stream()
                .map(dddId -> new Titelnummer(dddId.getValue()))
                .collect(Collectors.toList())
        );
    }

}
