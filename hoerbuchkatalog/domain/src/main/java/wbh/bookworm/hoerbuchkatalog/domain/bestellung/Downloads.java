/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    private static final int AUSLEIHZEITRAUM_TAGE = 30;

    @JsonProperty
    private final Hoerernummer hoerernummer;

    @JsonProperty
    private final Map<Titelnummer, Details> titelnummern;

    private final transient Predicate<Map.Entry<Titelnummer, Details>> anzahlBestellungenHeutePredicate;

    private final transient Predicate<Details> anzahlBestellungenAusleihzeitraumPredicate;

    private final transient Predicate<Map.Entry<Titelnummer, Details>> anzahlDownloadsProHoerbuchPredicate;

    private final transient Rules rules;

    @JsonProperty
    private int anzahlBestellungenProAusleihzeitraum;

    @JsonProperty
    private int anzahlBestellungenProTag;

    @JsonProperty
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
                     @JsonProperty(value = "anzahlBestellungenProAusleihzeitraum", defaultValue = "30") final int anzahlBestellungenProAusleihzeitraum,
                     @JsonProperty(value = "anzahlBestellungenProTag", defaultValue = "10") final int anzahlBestellungenProTag,
                     @JsonProperty(value = "anzahlDownloadsProHoerbuch", defaultValue = "5") final int anzahlDownloadsProHoerbuch,
                     @JsonProperty("titelnummern") final Map<Titelnummer, Details> titelnummern) {
        super(downloadsId);
        this.hoerernummer = hoerernummer;
        this.titelnummern = new HashMap<>();
        if (!titelnummern.isEmpty()) {
            this.titelnummern.putAll(titelnummern);
        }
        this.anzahlBestellungenProAusleihzeitraum = anzahlBestellungenProAusleihzeitraum == 0
                ? 30 : anzahlBestellungenProAusleihzeitraum;
        this.anzahlBestellungenProTag = anzahlBestellungenProTag == 0
                ? 10 : anzahlBestellungenProTag;
        this.anzahlDownloadsProHoerbuch = anzahlDownloadsProHoerbuch == 0
                ? 5 : anzahlDownloadsProHoerbuch;
        anzahlBestellungenAusleihzeitraumPredicate = details ->
                details.getAusgeliehenAm().isAfter(LocalDateTime.now().toLocalDate()
                        .atStartOfDay().minusDays(AUSLEIHZEITRAUM_TAGE));
        anzahlBestellungenHeutePredicate = entry ->
                isSameDay(LocalDateTime.now(), entry.getValue().getAusgeliehenAm());
        anzahlDownloadsProHoerbuchPredicate = entry ->
                entry.getValue().getAnzahlDownloads() < anzahlDownloadsProHoerbuch;
        this.rules = new Rules();
    }

    public static void main(String[] args) {
        final Downloads downloads = new Downloads(new DownloadsId("99998-Downloads"),
                new Hoerernummer("99998"),
                30, 10, 5,
                Collections.emptyMap());
        final Titelnummer titel21052 = Titelnummer.of("21052");
        System.out.println("Neue Bestellung erlaubt: " + downloads.neueBestellungErlaubt());
        System.out.println("21052 im Ausleihzeitraum: " + downloads.imAusleihzeitraumEnthalten(titel21052));
        System.out.println("Download 21052 erlaubt: " + downloads.downloadErlaubt(titel21052));
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

    @JsonIgnore
    public Map<Titelnummer, Details> titelnummernImAusleihzeitraum() {
        return titelnummern.entrySet()
                .stream()
                .filter(e -> anzahlBestellungenAusleihzeitraumPredicate.test(e.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public int anzahlBestellungen() {
        return titelnummernImAusleihzeitraum().size();
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
                .map(Map.Entry::getValue)
                .filter(anzahlBestellungenAusleihzeitraumPredicate)
                .count();
    }

    public Integer anzahlDownloads(final Titelnummer titelnummer) {
        return titelnummern.entrySet().stream()
                .filter(entry -> entry.getKey().equals(titelnummer))
                .filter(entry -> anzahlBestellungenAusleihzeitraumPredicate.test(entry.getValue()))
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Details::getAnzahlDownloads)
                .orElse(0);
    }

    public boolean imAusleihzeitraumEnthalten(final Titelnummer titelnummer) {
        final List<Details> titel = titelnummern.entrySet().stream()
                .filter(entry -> entry.getKey().equals(titelnummer))
                .map(Map.Entry::getValue)
                .collect(Collectors.toUnmodifiableList());
        return !titel.isEmpty()
                && titel.stream().allMatch(anzahlBestellungenAusleihzeitraumPredicate);
    }

    public boolean ausleihen(final Titelnummer titelnummer) {
        titelnummern.putIfAbsent(titelnummer, Details.neu());
        final Details details = titelnummern.get(titelnummer);
        // Ausleihdatum aktualisieren?
        if (details.rueckgabeBis.isBefore(LocalDateTime.now())) {
            details.jetztAusleihen();
        }
        details.zaehleDownload();
        LOGGER.info("Hörbuch '{}' zu Downloads '{}' hinzugefügt/Download gezählt: {}",
                titelnummer, this.getDomainId(), details.getAnzahlDownloads());
        return true;
    }

    public void zurueckgeben(final Titelnummer titelnummer) {
        titelnummern.remove(titelnummer);
        LOGGER.info("Hörbuch '{}' aus Downloads '{}' entfernt/Hörbuch zurückgegeben",
                titelnummer, this.getDomainId());
    }

    public void freiputzen() {
        titelnummern.clear();
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
        final boolean kannHeruntergeladenWerden = rules.titelKannHeruntergeladenWerden.isSatisfied(titelnummer);
        final boolean kannBestelltWerden = rules.titelKannBestelltWerden.isSatisfied(titelnummer);
        final boolean bestellungErlaubt = rules.bestellungErlaubt.isSatisfied(titelnummer);
        final boolean downloadErlaubt = rules.downloadErlaubt.isSatisfied(titelnummer);
        LOGGER.debug("Hörer {}: Titelnummer {}," +
                        " imAusleihzeitraum={}, bestellungErlaubt={}, downloadErlaubt={}," +
                        " {} heruntergeladen, {} bestellt werden",
                hoerernummer, titelnummer,
                rules.imAusleihzeitraum.isSatisfied(titelnummer),
                bestellungErlaubt,
                downloadErlaubt,
                kannHeruntergeladenWerden ? "kann" : "kann nicht",
                kannBestelltWerden ? "kann" : "kann nicht");
        return bestellungErlaubt || downloadErlaubt;
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

    public boolean imAusleihzeitraum(final Titelnummer titelnummer) {
        return rules.imAusleihzeitraum.isSatisfied(titelnummer);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Details implements Comparable<Details> {

        private LocalDateTime ausgeliehenAm;

        private LocalDateTime rueckgabeBis;

        private int anzahlDownloads;

        @JsonCreator
        public Details(@JsonProperty("ausgeliehenAm") final LocalDateTime ausgeliehenAm,
                       @JsonProperty("rueckgabeBis") final LocalDateTime rueckgabeBis,
                       @JsonProperty("anzahlDownloads") final int anzahlDownloads) {
            this.ausgeliehenAm = ausgeliehenAm;
            this.rueckgabeBis = Optional.ofNullable(rueckgabeBis)
                    .orElse(ausgeliehenAm.plusDays(AUSLEIHZEITRAUM_TAGE));
            this.anzahlDownloads = anzahlDownloads;
        }

        private Details() {
        }

        public static Details neu() {
            final Details details = new Details();
            details.jetztAusleihen();
            return details;
        }

        public void jetztAusleihen() {
            ausgeliehenAm = LocalDateTime.now();
            rueckgabeBis = ausgeliehenAm.plusDays(AUSLEIHZEITRAUM_TAGE);
            anzahlDownloads = 0;
        }

        public LocalDateTime getAusgeliehenAm() {
            return ausgeliehenAm;
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

        public void resetDownloads() {
            anzahlDownloads = 0;
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

        final Specification<Titelnummer> imAusleihzeitraum = new ImAusleihzeitraum();

        final Specification<Titelnummer> bestellungErlaubt = new BestellungErlaubt();

        // Fall 1:
        // - Titel ist nicht in der Download-Liste im Ausleihzeitraum enthalten und
        // - Kontingent (pro Tag, pro Ausleihzeitraum) erlaubt download
        // - Download-Zähler wird auf 1 gesetzt
        final Specification<Titelnummer> titelKannBestelltWerden =
                not(new ImAusleihzeitraum()).and(new BestellungErlaubt());

        final Specification<Titelnummer> downloadErlaubt = new DownloadErlaubt();

        // Fall 2:
        // - Titel ist in der Download-Liste im Ausleihzeitraum enthalten und
        // - Ausleihzeitraum passt und
        // - es wurden noch nicht 5 Downloads getätigt
        final Specification<Titelnummer> titelKannHeruntergeladenWerden =
                new ImAusleihzeitraum().and(new DownloadErlaubt());

    }

    private class ImAusleihzeitraum implements Specification<Titelnummer> {

        private final Logger LOGGER = LoggerFactory.getLogger(Downloads.ImAusleihzeitraum.class);

        @Override
        public boolean isSatisfied(final Titelnummer titelnummer) {
            final List<Details> details = titelnummern.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(titelnummer))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toUnmodifiableList());
            return !details.isEmpty()
                    && details.stream().allMatch(anzahlBestellungenAusleihzeitraumPredicate);
        }

    }

    private class BestellungErlaubt implements Specification<Titelnummer> {

        private final Logger LOGGER = LoggerFactory.getLogger(Downloads.BestellungErlaubt.class);

        @Override
        public boolean isSatisfied(final Titelnummer titelnummer) {
            if (hoerernummer.isUnbekannt()) {
                LOGGER.warn("Hörer 00000 darf {} nicht bestellen", titelnummer);
                return false;
            }
            LOGGER.debug("Hörer {}: {} heute bestellt, {} im Ausleihzeitraum bestellt", hoerernummer,
                    anzahlHeutigerBestellungen(), anzahlBestellungenImAusleihzeitraum());
            return anzahlHeutigerBestellungen() < anzahlBestellungenProTag
                    && anzahlBestellungenImAusleihzeitraum() < anzahlBestellungenProAusleihzeitraum;
        }

    }

    private class DownloadErlaubt implements Specification<Titelnummer> {

        private final Logger LOGGER = LoggerFactory.getLogger(Downloads.DownloadErlaubt.class);

        @Override
        public boolean isSatisfied(final Titelnummer titelnummer) {
            if (hoerernummer.isUnbekannt()) {
                LOGGER.warn("Hörer 00000 darf {} nicht herunterladen", titelnummer);
                return false;
            }
            final Integer i = anzahlDownloads(titelnummer);
            LOGGER.debug("Hörer {}: Titel {} hat {} Downloads", hoerernummer,
                    titelnummer, i);
            return i < anzahlDownloadsProHoerbuch;
        }

    }

}
