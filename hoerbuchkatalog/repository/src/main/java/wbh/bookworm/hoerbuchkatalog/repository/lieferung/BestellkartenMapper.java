/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.hoerbuchkatalog.repository.as400.Datenformat;

import aoc.mikrokosmos.io.dataformat.CsvFormat;
import aoc.mikrokosmos.io.dataformat.CsvParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Lazy
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings({"squid:S1192", "java:S1192"})
final class BestellkartenMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellkartenMapper.class);

    private static final CsvFormat BKSTP_CSVFORMAT = new CsvFormat();

    static {
        BKSTP_CSVFORMAT.addField("BKHNR", "HÖRER-NR");
        BKSTP_CSVFORMAT.addField("BKAIN", "AUS-INDEX");
        BKSTP_CSVFORMAT.addField("BKAOP", "OP");
        BKSTP_CSVFORMAT.addField("BKP", "BEST", 1, 400);
        BKSTP_CSVFORMAT.addField("BKPDAT", "LETZT.BEST.DATUM");
    }

    private static final CsvParser bkstp = new CsvParser(BKSTP_CSVFORMAT);

    private Map<Hoerernummer, List<Bestellkarte>> bestellkarten;

    public BestellkartenMapper(final CdLieferungRepositoryConfig cdLieferungRepositoryConfig) {
        leseAs400Datei(
                StandardCharsets.ISO_8859_1, 9_000,
                cdLieferungRepositoryConfig.getDirectory().resolve("bkstp.csv"));
    }

    private static Bestellkarte bestellkarte(final String hoerernummer,
                                             final String titelnummer,
                                             final String letztesBestelldatum) {
        return new Bestellkarte(new Hoerernummer(hoerernummer),
                null != titelnummer ? new Titelnummer(titelnummer) : null,
                Datenformat.localDateOf(hoerernummer, "Letztes Bestelldatum", letztesBestelldatum));
    }

    private void leseAs400Datei(final Charset csvCharset, int expectedLineCount,
                                final Path bkstpCsv) {
        if (Files.exists(bkstpCsv)) {
            LOGGER.info("Lese CSV-Datei {} mit Zeichensatz {} und erwarteter Anzahl Zeilen {}",
                    bkstpCsv, csvCharset, expectedLineCount);
            LocalDateTime start = LocalDateTime.now();
            bkstp.flatParseLines(csvCharset, expectedLineCount, bkstpCsv);
            LOGGER.info("CSV-Datei {} gelesen (Anzahl: {}), es dauerte {} ms",
                    bkstpCsv, bkstp.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
            start = LocalDateTime.now();
            bestellkarten = bkstp.getRows()
                    .parallelStream()
                    .map(this::parseAlleBestellkarten)
                    .collect(Collectors.toUnmodifiableMap(o -> o.get(0).getHoerernummer(),
                            o -> o.subList(1, o.size())));
            LOGGER.info("Bestellkarten erzeugt ({} BKSTP -> {} Hörer), es dauerte {} ms",
                    bkstp.size(), bestellkarten.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
        } else {
            LOGGER.warn("CSV-Dateien {} nicht gefunden/lesbar", bkstpCsv);
            bestellkarten = Collections.emptyMap();
        }
    }

    public boolean hatDatenEingelesen() {
        return !bestellkarten.isEmpty();
    }

    private List<Bestellkarte> parseAlleBestellkarten(final String[] bkstpRow) {
        final List<Bestellkarte> alleBestellkarten = IntStream.range(1, 400 + 1)
                .mapToObj(i -> {
                    final String bkp = bkstp.getValue(bkstpRow, "BKP" + i);
                    try {
                        final int bpkZahl = Integer.parseInt(bkp);
                        if (bpkZahl > 0 && bpkZahl < 888888) {
                            return bestellkarte(
                                    bkstp.getValue(bkstpRow, "BKHNR"),
                                    bkp,
                                    bkstp.getValue(bkstpRow, "BKPDAT"));
                        } else {
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warn("Kann BKSTP-Feld BKP{} nicht parsen: {}", i, bkp);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // TODO Jede Bestellkarte enthält...
        // Erste Bestellkarte enthält letztes Bestelldatum
        alleBestellkarten.add(0, bestellkarte(
                bkstp.getValue(bkstpRow, "BKHNR"),
                null,
                bkstp.getValue(bkstpRow, "BKPDAT")));
        return alleBestellkarten;
    }

    List<Bestellkarte> bestellkartenFuer(final Hoerernummer hoerernummer) {
        return null != bestellkarten
                ? bestellkarten.getOrDefault(hoerernummer, Collections.emptyList())
                : Collections.emptyList();
    }

}
