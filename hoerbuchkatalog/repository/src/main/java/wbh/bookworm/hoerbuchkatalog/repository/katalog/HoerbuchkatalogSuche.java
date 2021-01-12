/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainId;
import aoc.mikrokosmos.ddd.search.BooleanQueryBuilder;
import aoc.mikrokosmos.ddd.search.LuceneIndex;
import aoc.mikrokosmos.ddd.search.LuceneQuery;
import aoc.mikrokosmos.ddd.search.QueryParameters;
import aoc.mikrokosmos.lang.strings.StringNormalizer;

/**
 * Lucene reserved characters: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
 */
final class HoerbuchkatalogSuche {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogSuche.class);

    private final LuceneIndex luceneIndex;

    HoerbuchkatalogSuche(final ApplicationContext applicationContext,
                         final DomainId<String> hoerbuchkatalogDomainId) {
        luceneIndex = applicationContext.getBean(LuceneIndex.class, hoerbuchkatalogDomainId.getValue());
    }

    void indiziere(final Set<Hoerbuch> hoerbuecher) {
        LOGGER.trace("Indiziere {} Hörbücher", hoerbuecher.size());
        if (luceneIndex.hasIndex()) {
            LOGGER.info("Hörbuchkatalog mit {} Einträgen wurde bereits indiziert", hoerbuecher.size());
        } else {
            luceneIndex.deleteIndex()
                    .add(hoerbuecher,
                            "titelnummer",
                            new String[]{
                                    Suchparameter.Feld.TITELNUMMER.luceneName(),
                                    Suchparameter.Feld.SACHGEBIET.luceneName()
                            },
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

    private List<Titelnummer> titelnummern(final LuceneQuery.Result result) {
        return result.getDomainIds()
                .stream()
                .map(dddId -> new Titelnummer(dddId.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    Suchergebnis suchen(final Suchparameter suchparameter) {
        Objects.requireNonNull(suchparameter);
        if (!suchparameter.isWerteVorhanden()) {
            return Suchergebnis.leeresSuchergebnis(suchparameter);
        }
        final BooleanQueryBuilder booleanQueryBuilder = new BooleanQueryBuilder();
        sachgebiet(booleanQueryBuilder, suchparameter);
        einstelldatum(booleanQueryBuilder, suchparameter);
        final Suchparameter ohneSachgebietUndEinstelldatum = new Suchparameter(suchparameter);
        ohneSachgebietUndEinstelldatum
                .entfernen(Suchparameter.Feld.STICHWORT)
                .entfernen(Suchparameter.Feld.SACHGEBIET)
                .entfernen(Suchparameter.Feld.EINSTELLDATUM);
        lowercaseWildcard(booleanQueryBuilder, ohneSachgebietUndEinstelldatum);
        stichwort(booleanQueryBuilder, suchparameter);
        final LuceneQuery.Result result = LuceneQuery.query(luceneIndex,
                booleanQueryBuilder.build(), suchparameter.getMaxAnzahlSuchergebnisse(),
                Suchparameter.Feld.AUTOR.name(), Suchparameter.Feld.TITEL.name());
        final List<Titelnummer> titelnummern = titelnummern(result);
        final Suchergebnis suchergebnis = new Suchergebnis(suchparameter, titelnummern, result.getTotalMatchingCount());
        LOGGER.info("Suche nach '{}' ergab {} Treffer", suchparameter, suchergebnis.getAnzahl());
        return suchergebnis;
    }

    private void stichwort(final BooleanQueryBuilder booleanQueryBuilder,
                           final Suchparameter suchparameter) {
        if (suchparameter.wertVorhanden(Suchparameter.Feld.STICHWORT)) {
            final String stichwort = suchparameter.wert(Suchparameter.Feld.STICHWORT);
            final BooleanQuery.Builder stichwoerterQuery = new BooleanQuery.Builder();
            final String[] words = stichwort.split("\\s+");
            stichwoerterQuery.setMinimumNumberShouldMatch(words.length);
            for (final String word : words) {
                final BooleanQuery.Builder stichwortQuery = new BooleanQuery.Builder();
                stichwortQuery.setMinimumNumberShouldMatch(1);
                stichwortQuery.add(new TermQuery(new Term(Suchparameter.Feld.TITELNUMMER.luceneName(), word)),
                        BooleanClause.Occur.SHOULD);
                final String stichwortWildcard = String.format("*%s*", StringNormalizer.normalize(word.toLowerCase()));
                stichwortQuery.add(new WildcardQuery(new Term(Suchparameter.Feld.AUTOR.luceneName(), stichwortWildcard)),
                        BooleanClause.Occur.SHOULD);
                stichwortQuery.add(new WildcardQuery(new Term(Suchparameter.Feld.SPRECHER1.luceneName(), stichwortWildcard)),
                        BooleanClause.Occur.SHOULD);
                stichwortQuery.add(new WildcardQuery(new Term(Suchparameter.Feld.SPRECHER2.luceneName(), stichwortWildcard)),
                        BooleanClause.Occur.SHOULD);
                stichwortQuery.add(new WildcardQuery(new Term(Suchparameter.Feld.TITEL.luceneName(), stichwortWildcard)),
                        BooleanClause.Occur.SHOULD);
                stichwortQuery.add(new WildcardQuery(new Term(Suchparameter.Feld.UNTERTITEL.luceneName(), stichwortWildcard)),
                        BooleanClause.Occur.SHOULD);
                stichwortQuery.add(new WildcardQuery(new Term(Suchparameter.Feld.ERLAEUTERUNG.luceneName(), stichwortWildcard)),
                        BooleanClause.Occur.SHOULD);
                stichwortQuery.add(new WildcardQuery(new Term(Suchparameter.Feld.SUCHWOERTER.luceneName(), stichwortWildcard)),
                        BooleanClause.Occur.SHOULD);
                stichwoerterQuery.add(stichwortQuery.build(), BooleanClause.Occur.SHOULD);
            }
            booleanQueryBuilder.add(stichwoerterQuery.build(), BooleanClause.Occur.MUST);
        }
    }

    private static void einstelldatum(final BooleanQueryBuilder booleanQueryBuilder,
                                      final Suchparameter suchparameter) {
        if (suchparameter.wertVorhanden(Suchparameter.Feld.EINSTELLDATUM)) {
            final String wert = suchparameter.wert(Suchparameter.Feld.EINSTELLDATUM);
            parseDeutschesDatum(wert)
                    .or(() -> parseMonatJahr(wert))
                    .or(Optional::empty)
                    .ifPresent(localDate ->
                            booleanQueryBuilder.addRange(new QueryParameters.Field(
                                            Suchparameter.Feld.EINSTELLDATUM.name(), QueryParameters.Occur.MUST),
                                    localDate, null));
        }
    }

    private static void sachgebiet(final BooleanQueryBuilder booleanQueryBuilder,
                                   final Suchparameter suchparameter) {
        if (suchparameter.wertVorhanden(Suchparameter.Feld.SACHGEBIET)) {
            booleanQueryBuilder.addExactPhrase(new QueryParameters.Field(
                            Suchparameter.Feld.SACHGEBIET.name(), QueryParameters.Occur.MUST),
                    suchparameter.wert(Suchparameter.Feld.SACHGEBIET));
        }
    }

    private static void lowercaseWildcard(final BooleanQueryBuilder booleanQueryBuilder,
                                          final Suchparameter suchparameter) {
        suchparameter.getFelderMitWerten().keySet()
                .stream()
                .filter(k -> !suchparameter.wert(k).isBlank())
                .forEach(k -> booleanQueryBuilder.addLowercaseWildcard(
                        new QueryParameters.Field(k.name(), QueryParameters.Occur.MUST),
                        suchparameter.wert(k)));
    }

    private static final Pattern DEUTSCHES_DATUM_PATTERN = Pattern.compile("\\d{2}.\\d{2}.\\d{4}");
    private static Optional<LocalDate> parseDeutschesDatum(final String wert) {
        if (DEUTSCHES_DATUM_PATTERN.matcher(wert).matches()) {
            try {
                return Optional.of(LocalDate.parse(wert,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)));
            } catch (Exception e) {
                // ignore
            }
        }
        return Optional.empty();
    }

    private static final Pattern MONAT_JAHR_PATTERN = Pattern.compile("\\w{3,} \\d{4}");
    private static Optional<LocalDate> parseMonatJahr(final String wert) {
        String s = StringNormalizer.normalize(wert);
        if (MONAT_JAHR_PATTERN.matcher(s).matches()) {
            try {
                final String[] split = wert.split("[ ]");
                final String format = String.format("1 %1$.3s %2$s", split[0], split[1]);
                return Optional.of(LocalDate.parse(format,
                        DateTimeFormatter.ofPattern("d LLL yyyy", Locale.GERMANY)));
            } catch (Exception e) {
                // ignore
            }
        }
        return Optional.empty();
    }

}
