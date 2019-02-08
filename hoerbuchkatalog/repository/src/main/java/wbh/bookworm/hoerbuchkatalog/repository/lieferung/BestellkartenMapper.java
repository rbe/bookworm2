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

import aoc.tools.datatransfer.CsvFormat;
import aoc.tools.datatransfer.CsvParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
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

    void leseAs400Datei(final Charset csvCharset, int expectedLineCount,
                        final Path bkstpCsv) {
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
    }

    private static Bestellkarte bestellkarte(final String hoerernummer,
                                             final String titelnummer, final String letztesBestelldatum) {
        return new Bestellkarte(new Hoerernummer(hoerernummer),
                null != titelnummer ? new Titelnummer(titelnummer) : null,
                Datenformat.localDateOf(hoerernummer, "Letztes Bestelldatum", letztesBestelldatum));
    }

    private List<Bestellkarte> parseAlleBestellkarten(final String[] bkrxstpRow) {
        final List<Bestellkarte> alleBestellkarten = IntStream.range(1, 400 + 1)
                .mapToObj(i -> {
                    final String bkp = bkstp.getValue(bkrxstpRow, "BKP" + i);
                    if (!"0".equals(bkp)) {
                        return bestellkarte(
                                bkstp.getValue(bkrxstpRow, "BKHNR"),
                                bkp,
                                bkstp.getValue(bkrxstpRow, "BKPDAT"));
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // TODO Jede Bestellkarte enthält...
        // Erste Bestellkarte enthält letztes Bestelldatum
        alleBestellkarten.add(0, bestellkarte(
                bkstp.getValue(bkrxstpRow, "BKHNR"),
                null,
                bkstp.getValue(bkrxstpRow, "BKPDAT")));
        return alleBestellkarten;
    }

    List<Bestellkarte> bestellkartenFuer(final Hoerernummer hoerernummer) {
        return bestellkarten.get(hoerernummer);
    }

}
