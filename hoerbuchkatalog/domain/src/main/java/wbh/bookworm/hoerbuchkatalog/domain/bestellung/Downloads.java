/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainAggregate;

public final class Downloads extends DomainAggregate<Downloads, DownloadsId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Downloads.class);

    @JsonProperty
    private final Hoerernummer hoerernummer;

    private final transient Map<Titelnummer, Details> titelnummern;

    private transient Predicate<Map.Entry<Titelnummer, Details>> ausleihzeitraumPredicate;

    /**
     * Copy constructor
     */
    public Downloads(final Downloads downloads) {
        super(new DownloadsId(downloads.domainId.getValue()));
        this.hoerernummer = new Hoerernummer(downloads.hoerernummer.getValue());
        this.titelnummern = new HashMap<>();
        this.titelnummern.putAll(downloads.titelnummern);
        init();
    }

    public Downloads(final DownloadsId downloadsId, final Hoerernummer hoerernummer) {
        this(downloadsId, hoerernummer, new HashMap<>());
    }

    @JsonCreator
    public Downloads(@JsonProperty("domainId") final DownloadsId downloadsId,
                     @JsonProperty("hoerernummer") final Hoerernummer hoerernummer,
                     @JsonProperty("titelnummern") final Map<Titelnummer, Details> titelnummern) {
        super(downloadsId);
        this.hoerernummer = hoerernummer;
        this.titelnummern = titelnummern;
        init();
    }

    private void init() {
        final LocalDateTime beginnAusleihzeitraum = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(30L);
        ausleihzeitraumPredicate = entry ->
                entry.getValue().getAusgeliehenAm().isAfter(beginnAusleihzeitraum);
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Map<Titelnummer, Details> getTitelnummern() {
        return titelnummern.entrySet()
                .stream()
                .filter(ausleihzeitraumPredicate)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public int getAnzahl() {
        return getTitelnummern().size();
    }

    public long anzahlHeute() {
        final LocalDateTime now = LocalDateTime.now();
        return titelnummern.entrySet().stream()
                .filter(entry -> isSameDay(now, entry.getValue().getAusgeliehenAm()))
                .count();
    }

    private boolean isSameDay(final LocalDateTime date1, final LocalDateTime date2) {
        return date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDayOfMonth() == date2.getDayOfMonth();
    }

    public long anzahlAusleihzeitraum() {
        return titelnummern.entrySet().stream()
                .filter(ausleihzeitraumPredicate)
                .count();
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        final boolean bereitsVorhanden = getTitelnummern().containsKey(titelnummer);
        LOGGER.trace("Downloads '{}' enthält Hörbuch '{}': '{}'", this.getDomainId(), titelnummer, bereitsVorhanden);
        return bereitsVorhanden;
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        titelnummern.put(titelnummer, new Details(LocalDateTime.now()));
        LOGGER.info("Hörbuch '{}' zu Downloads '{}' hinzugefügt", titelnummer, this.getDomainId());
    }

    public LocalDateTime ausgeliehenAm(final Titelnummer titelnummer) {
        final boolean vorhanden = titelnummern.containsKey(titelnummer);
        return vorhanden ? titelnummern.get(titelnummer).getAusgeliehenAm() : null;
    }

    public LocalDateTime rueckgabeBis(final Titelnummer titelnummer) {
        final boolean vorhanden = titelnummern.containsKey(titelnummer);
        return vorhanden ? titelnummern.get(titelnummer).getRueckgabeBis() : null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final Downloads merkliste = (Downloads) o;
        return Objects.equals(hoerernummer, merkliste.hoerernummer) &&
                Objects.equals(titelnummern, merkliste.titelnummern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerernummer, titelnummern);
    }

    @Override
    public String toString() {
        return String.format("Downloads{domainId=%s, merklisteId=%s, hoerernummer=%s, titelnummern=%s}",
                domainId, domainId, hoerernummer, titelnummern);
    }

    public static class Details implements Comparable<Details> {

        private final LocalDateTime ausgeliehenAm;

        @JsonIgnore
        private final LocalDateTime rueckgabeBis;

        @JsonCreator
        public Details(@JsonProperty("ausgeliehenAm") final LocalDateTime ausgeliehenAm) {
            this.ausgeliehenAm = ausgeliehenAm;
            this.rueckgabeBis = ausgeliehenAm.plusMonths(1);
        }

        public LocalDateTime getAusgeliehenAm() {
            return ausgeliehenAm;
        }

        public LocalDateTime getRueckgabeBis() {
            return rueckgabeBis;
        }

        @Override
        public int compareTo(final Details o) {
            return ausgeliehenAm.compareTo(o.ausgeliehenAm);
        }

    }

}
