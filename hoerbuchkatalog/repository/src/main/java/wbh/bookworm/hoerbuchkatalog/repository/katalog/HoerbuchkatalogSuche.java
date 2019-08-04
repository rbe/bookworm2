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
import aoc.strings.StringNormalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class HoerbuchkatalogSuche {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogSuche.class);

    private final LuceneIndex luceneIndex;

    private final int anzahlSuchergebnisse;

    private static final class StichwortLuceneAbfrage {

        private static <T> List<List<T>> computeCombinations2(List<List<T>> lists) {
            List<List<T>> combinations = Arrays.asList(Arrays.asList());
            for (List<T> list : lists) {
                List<List<T>> extraColumnCombinations = new ArrayList<>();
                for (List<T> combination : combinations) {
                    for (T element : list) {
                        List<T> newCombination = new ArrayList<>(combination);
                        newCombination.add(element);
                        extraColumnCombinations.add(newCombination);
                    }
                }
                combinations = extraColumnCombinations;
            }
            return combinations;
        }

        public static String makeQuery(String[] stichwoerter) {
            final List<List<String>> product = computeCombinations2(Arrays.asList(
                    List.of(Suchparameter.Feld.TITELNUMMER.luceneName(),
                            Suchparameter.Feld.AUTOR.luceneName(),
                            Suchparameter.Feld.TITEL.luceneName(),
                            Suchparameter.Feld.UNTERTITEL.luceneName(),
                            Suchparameter.Feld.ERLAEUTERUNG.luceneName(),
                            Suchparameter.Feld.SUCHWOERTER.luceneName()
                    ),
                    Arrays.asList(stichwoerter)));
            final Map<String, List<String>> felderMitStichwoertern = product.stream()
                    .map(elt -> String.format("%s:%s*", elt.get(0), elt.get(1).toLowerCase()))
                    .collect(Collectors.groupingBy(elt -> elt.substring(0, elt.indexOf(':'))));
            return felderMitStichwoertern.values()
                    .stream()
                    .map(v -> {
                        final StringBuilder sb = new StringBuilder("(");
                        for (int i = 0; i < v.size(); i++) {
                            final String elt = v.get(i);
                            sb.append(elt);
                            if (i < v.size() - 1) {
                                sb.append(" AND ");
                            }
                        }
                        sb.append(")");
                        return sb.toString();
                    })
                    .collect(Collectors.joining(" OR "));
        }

    }

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

    Suchergebnis sucheNachStichwort(final String stichwort) {
        Objects.requireNonNull(stichwort);
        if (stichwort.isBlank()) {
            return Suchergebnis.leeresSuchergebnis(stichwort);
        }
        // TODO final String stichwort = suchparameter.wert(Feld.STICHWORT);
        // Lucene reserved characters: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
        final String normalizedStichwort = Normalizer.normalize(stichwort, Normalizer.Form.NFD)
                .replaceAll("[^A-Za-zäöüß0-9 ,-]", "");
        LOGGER.trace("Suche nach Stichwort '{}'", stichwort);
        final String[] stichwoerter = Arrays.stream(stichwort.split("[\\s,-]"))
                .filter(elt -> !elt.isBlank())
                .toArray(String[]::new);
        final String query = StichwortLuceneAbfrage.makeQuery(stichwoerter);
        final LuceneQuery.Result result = LuceneQuery.query(this.luceneIndex,
                query, anzahlSuchergebnisse,
                Suchparameter.Feld.AUTOR.name(), Suchparameter.Feld.TITEL.name());
        final List<Titelnummer> titelnummern = result.getDomainIds()
                .stream()
                .map(dddId -> new Titelnummer(dddId.getValue()))
                .collect(Collectors.toUnmodifiableList());
        LOGGER.debug("Lucene Query {} ergab {} Treffer", query, titelnummern.size());
        final Suchparameter suchparameter = new Suchparameter().hinzufuegen(
                Suchparameter.Feld.STICHWORT, stichwort);
        final Suchergebnis suchergebnis = new Suchergebnis(
                suchparameter, titelnummern, result.getTotalMatchingCount());
        LOGGER.debug("Suche nach Stichwort '{}' ergab {}", stichwort, suchergebnis);
        return suchergebnis;
    }

    Suchergebnis suchen(final Suchparameter suchparameter) {
        Objects.requireNonNull(suchparameter);
        if (!suchparameter.isWerteVorhanden()) {
            return Suchergebnis.leeresSuchergebnis(suchparameter);
        }
        LOGGER.info("Suche nach '{}'", suchparameter);
        final BooleanQueryBuilder booleanQueryBuilder = new BooleanQueryBuilder();
        if (suchparameter.wertVorhanden(Suchparameter.Feld.SACHGEBIET)) {
            booleanQueryBuilder.addExactPhrase(new QueryParameters.Field(
                            Suchparameter.Feld.SACHGEBIET.name(), QueryParameters.Occur.MUST),
                    suchparameter.wert(Suchparameter.Feld.SACHGEBIET));
        }
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
        final Suchparameter ohneSachgebietUndEinstelldatum = new Suchparameter(suchparameter);
        ohneSachgebietUndEinstelldatum.entfernen(Suchparameter.Feld.SACHGEBIET).entfernen(Suchparameter.Feld.EINSTELLDATUM);
        ohneSachgebietUndEinstelldatum.getFelderMitWerten().keySet()
                .stream()
                .filter(k -> !suchparameter.wert(k).isBlank())
                .forEach(k -> booleanQueryBuilder.addLowercaseWildcard(
                        new QueryParameters.Field(k.name(), QueryParameters.Occur.MUST),
                        suchparameter.wert(k)));
        final LuceneQuery.Result result = LuceneQuery.query(
                luceneIndex, booleanQueryBuilder, anzahlSuchergebnisse,
                Suchparameter.Feld.AUTOR.name(), Suchparameter.Feld.TITEL.name());
        final List<Titelnummer> titelnummern = result.getDomainIds()
                .stream()
                .map(dddId -> new Titelnummer(dddId.getValue()))
                .collect(Collectors.toList());
        return new Suchergebnis(suchparameter, titelnummern, result.getTotalMatchingCount());
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
