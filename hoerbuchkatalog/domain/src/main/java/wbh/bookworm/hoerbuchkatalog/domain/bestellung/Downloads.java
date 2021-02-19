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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainAggregate;
import aoc.mikrokosmos.ddd.specification.Specification;

import static aoc.mikrokosmos.ddd.specification.Specification.not;

public final class Downloads extends DomainAggregate<Downloads, DownloadsId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Downloads.class);

    @JsonProperty
    private final Hoerernummer hoerernummer;

    private final HashMap<Titelnummer, Details> titelnummern;

    private final transient Predicate<Map.Entry<Titelnummer, Details>> anzahlBestellungenHeutePredicate;

    private final transient Predicate<Map.Entry<Titelnummer, Details>> anzahlBestellungenAusleihzeitraumPredicate;

    private final transient Predicate<Map.Entry<Titelnummer, Details>> anzahlDownloadsProHoerbuchPredicate;

    private final transient Rules rules;

    private int anzahlBestellungenProAusleihzeitraum;

    private int anzahlBestellungenProTag;

    private int anzahlDownloadsProHoerbuch;

    /**
     * Copy constructor
     */
    public Downloads(final Downloads downloads) {
        this(downloads.domainId, downloads.hoerernummer, downloads.anzahlBestellungenProAusleihzeitraum,
                downloads.anzahlBestellungenProTag, downloads.anzahlDownloadsProHoerbuch,
                downloads.titelnummern);
    }

    @JsonCreator
    public Downloads(@JsonProperty("domainId") final DownloadsId downloadsId,
                     @JsonProperty("hoerernummer") final Hoerernummer hoerernummer,
                     @JsonProperty("anzahlBestellungenProAusleihzeitraum") final int anzahlBestellungenProAusleihzeitraum,
                     @JsonProperty("anzahlBestellungenProTag") final int anzahlBestellungenProTag,
                     @JsonProperty("anzahlDownloadsProHoerbuch") final int anzahlDownloadsProHoerbuch,
                     @JsonProperty("titelnummern") final Map<Titelnummer, Details> titelnummern) {
        super(downloadsId);
        this.hoerernummer = hoerernummer;
        this.titelnummern = new HashMap<>();
        this.titelnummern.putAll(titelnummern);
        this.anzahlBestellungenProAusleihzeitraum = anzahlBestellungenProAusleihzeitraum;
        this.anzahlBestellungenProTag = anzahlBestellungenProTag;
        this.anzahlDownloadsProHoerbuch = anzahlDownloadsProHoerbuch;
        anzahlBestellungenAusleihzeitraumPredicate = entry ->
                entry.getValue().getAusgeliehenAm().isAfter(LocalDateTime.now().toLocalDate()
                        .atStartOfDay().minusDays(30));
        anzahlBestellungenHeutePredicate = entry ->
                isSameDay(LocalDateTime.now(), entry.getValue().getAusgeliehenAm());
        anzahlDownloadsProHoerbuchPredicate = entry ->
                entry.getValue().getAnzahlDownloads() < anzahlDownloadsProHoerbuch;
        this.rules = new Rules();
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public int getAnzahlBestellungenProAusleihzeitraum() {
        return anzahlBestellungenProAusleihzeitraum;
    }

    public void setAnzahlBestellungenProAusleihzeitraum(final int anzahlBestellungenProAusleihzeitraum) {
        this.anzahlBestellungenProAusleihzeitraum = anzahlBestellungenProAusleihzeitraum;
    }

    public int getAnzahlBestellungenProTag() {
        return anzahlBestellungenProTag;
    }

    public void setAnzahlBestellungenProTag(final int anzahlBestellungenProTag) {
        this.anzahlBestellungenProTag = anzahlBestellungenProTag;
    }

    public int getAnzahlDownloadsProHoerbuch() {
        return anzahlDownloadsProHoerbuch;
    }

    public void setAnzahlDownloadsProHoerbuch(final int anzahlDownloadsProHoerbuch) {
        this.anzahlDownloadsProHoerbuch = anzahlDownloadsProHoerbuch;
    }

    public Map<Titelnummer, Details> getTitelnummern() {
        return titelnummern.entrySet()
                .stream()
                .filter(anzahlBestellungenAusleihzeitraumPredicate)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public int anzahlBestellungen() {
        return getTitelnummern().size();
    }

    public long anzahlHeutigerBestellungen() {
        return titelnummern.entrySet().stream()
                .filter(anzahlBestellungenHeutePredicate)
                .count();
    }

    private boolean isSameDay(final LocalDateTime date1, final LocalDateTime date2) {
        return date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDayOfMonth() == date2.getDayOfMonth();
    }

    public long anzahlBestellungenImAusleihzeitraum() {
        return titelnummern.entrySet().stream()
                .filter(anzahlBestellungenAusleihzeitraumPredicate)
                .count();
    }

    public Integer anzahlDownloads(final Titelnummer titelnummer) {
        return titelnummern.entrySet().stream()
                .filter(entry -> entry.getKey().equals(titelnummer))
                .filter(anzahlBestellungenAusleihzeitraumPredicate)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Details::getAnzahlDownloads)
                .orElse(0);
    }

    public boolean imAusleihzeitraumEnthalten(final Titelnummer titelnummer) {
        return titelnummern.entrySet().stream()
                .filter(entry -> entry.getKey().equals(titelnummer))
                .allMatch(anzahlBestellungenAusleihzeitraumPredicate);
    }

    public boolean ausleihen(final Titelnummer titelnummer) {
        if (downloadErlaubt(titelnummer)) {
            titelnummern.putIfAbsent(titelnummer, new Details(LocalDateTime.now()));
            final Details details = titelnummern.get(titelnummer);
            // Ausleihdatum aktualisieren?
            if (details.rueckgabeBis.isAfter(LocalDateTime.now())) {
                details.jetztAusleihen();
            }
            details.zaehleDownload();
            LOGGER.info("Hörbuch '{}' zu Downloads '{}' hinzugefügt/Download gezählt: {}",
                    titelnummer, this.getDomainId(), details.getAnzahlDownloads());
            return true;
        } else {
            LOGGER.warn("Hörbuch '{}' wurde bereits heruntergeladen", titelnummer);
            return false;
        }
    }

    public void zurueckgeben(final Titelnummer titelnummer) {
        titelnummern.remove(titelnummer);
        LOGGER.info("Hörbuch '{}' aus Downloads '{}' entfernt/Hörbuch zurückgegeben",
                titelnummer, this.getDomainId());
    }

    public LocalDateTime ausgeliehenAm(final Titelnummer titelnummer) {
        final boolean vorhanden = titelnummern.containsKey(titelnummer);
        return vorhanden ? titelnummern.get(titelnummer).getAusgeliehenAm() : null;
    }

    public LocalDateTime rueckgabeBis(final Titelnummer titelnummer) {
        final boolean vorhanden = titelnummern.containsKey(titelnummer);
        return vorhanden ? titelnummern.get(titelnummer).getRueckgabeBis() : null;
    }

    public boolean neueBestellungErlaubt() {
        return rules.bestellungErlaubt.isSatisfied(null);
    }

    public boolean downloadErlaubt(final Titelnummer titelnummer) {
        return rules.titelKannHeruntergeladenWerden.or(rules.titelKannBestelltWerden)
                .isSatisfied(titelnummer);
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Details implements Comparable<Details> {

        public static final int DAYS = 30;

        private LocalDateTime ausgeliehenAm;

        @JsonIgnore
        private LocalDateTime rueckgabeBis;

        private int anzahlDownloads;

        public Details(@JsonProperty("ausgeliehenAm") final LocalDateTime ausgeliehenAm) {
            this.ausgeliehenAm = ausgeliehenAm;
            this.rueckgabeBis = ausgeliehenAm.plusDays(DAYS);
            this.anzahlDownloads = 0;
        }

        @JsonCreator
        public Details(@JsonProperty("ausgeliehenAm") final LocalDateTime ausgeliehenAm,
                       @JsonProperty("anzahlDownloads") final int anzahlDownloads) {
            this.ausgeliehenAm = ausgeliehenAm;
            this.rueckgabeBis = ausgeliehenAm.plusDays(DAYS);
            this.anzahlDownloads = anzahlDownloads;
        }

        public LocalDateTime getAusgeliehenAm() {
            return ausgeliehenAm;
        }

        public void jetztAusleihen() {
            ausgeliehenAm = LocalDateTime.now();
            rueckgabeBis = ausgeliehenAm.plusDays(DAYS);
            anzahlDownloads = 0;
        }

        public LocalDateTime getRueckgabeBis() {
            return rueckgabeBis;
        }

        public int getAnzahlDownloads() {
            return anzahlDownloads;
        }

        public void zaehleDownload() {
            anzahlDownloads++;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Details details = (Details) o;
            return anzahlDownloads == details.anzahlDownloads && ausgeliehenAm.equals(details.ausgeliehenAm);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ausgeliehenAm, anzahlDownloads);
        }

        @Override
        public int compareTo(final Details o) {
            return ausgeliehenAm.compareTo(o.ausgeliehenAm);
        }

    }

    private class Rules {

        final Specification<Titelnummer> bestellungErlaubt = new BestellungErlaubt();

        // Fall 1:
        // - Titel ist nicht in der Download-Liste im Ausleihzeitraum enthalten und
        // - Kontingent (pro Tag, pro Ausleihzeitraum) erlaubt download
        // - Download-Zähler wird auf 1 gesetzt
        final Specification<Titelnummer> titelKannBestelltWerden =
                not(new ImAusleihzeitraum()).and(new BestellungErlaubt());

        // Fall 2:
        // - Titel ist in der Download-Liste im Ausleihzeitraum enthalten und
        // - Ausleihzeitraum passt und
        // - es wurden noch nicht 5 Downloads getätigt
        final Specification<Titelnummer> titelKannHeruntergeladenWerden =
                new ImAusleihzeitraum().and(new DownloadErlaubt());

    }

    private class ImAusleihzeitraum implements Specification<Titelnummer> {

        @Override
        public boolean isSatisfied(final Titelnummer titelnummer) {
            return titelnummern.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(titelnummer))
                    .allMatch(anzahlBestellungenAusleihzeitraumPredicate);
        }

    }

    private class BestellungErlaubt implements Specification<Titelnummer> {

        @Override
        public boolean isSatisfied(final Titelnummer titelnummer) {
            return anzahlHeutigerBestellungen() < anzahlBestellungenProTag
                    && anzahlBestellungenImAusleihzeitraum() < anzahlBestellungenProAusleihzeitraum;
        }

    }

    private class DownloadErlaubt implements Specification<Titelnummer> {

        @Override
        public boolean isSatisfied(final Titelnummer titelnummer) {
            return anzahlDownloads(titelnummer) < anzahlDownloadsProHoerbuch;
        }

    }

}
