/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.hoerbuchkatalog.repository.as400.Datenformat;

import aoc.tools.datatransfer.CsvFormat;
import aoc.tools.datatransfer.CsvParser;
import aoc.tools.datatransfer.FileSplitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
final class ErledigteBestellkartenMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErledigteBestellkartenMapper.class);

    private static final CsvFormat BKRXSTP_CSVFORMAT = new CsvFormat();

    static {
        BKRXSTP_CSVFORMAT.addField("BEXLNR", "HÖRER-NR");
        BKRXSTP_CSVFORMAT.addField("BEXTIT", "TITEL-NR");
        BKRXSTP_CSVFORMAT.addField("BEXDAT", "AUSLEIH-DATUM");
        BKRXSTP_CSVFORMAT.addField("BEXKZ", "KENNZEICHEN");
    }

    private static final CsvParser bkrxstp = new CsvParser(BKRXSTP_CSVFORMAT);

    private Map<Hoerernummer, List<ErledigteBestellkarte>> erledigteBestellkarten;

    private final ExecutorService executorService;

    @Autowired
    ErledigteBestellkartenMapper(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    private static ErledigteBestellkarte erledigteBestellkarte(final String hoerernummer,
                                                               final String titelnummer, final String ausleihdatum) {
        return new ErledigteBestellkarte(new Hoerernummer(hoerernummer),
                new Titelnummer(titelnummer),
                Datenformat.localDateOf(hoerernummer, "Ausleihdatum", ausleihdatum));
    }

    void leseAs400Datei(final Charset csvCharset, int expectedLineCount,
                        final Path bkrxstpCsv) {
        LOGGER.info("Lese CSV-Datei {} mit Zeichensatz {} und erwarteter Anzahl Zeilen {}",
                bkrxstpCsv, csvCharset, expectedLineCount);
        LocalDateTime start = LocalDateTime.now();
        try {
            final Path[] splittedCsvFiles = new FileSplitter(executorService)
                    .split(bkrxstpCsv, csvCharset, expectedLineCount,
                            Runtime.getRuntime().availableProcessors() / 2);
            LOGGER.debug("Lese CSV-Dateien: {}", Arrays.asList(splittedCsvFiles));
            bkrxstp.flatParseLines(csvCharset, expectedLineCount, splittedCsvFiles);
            Arrays.asList(splittedCsvFiles).forEach(f -> {
                try {
                    LOGGER.info("Lösche CSV-Datei {}", f);
                    Files.delete(f);
                } catch (IOException e) {
                    LOGGER.error("Cannot delete " + f, e);
                }
            });
            LOGGER.info("CSV-Datei {} gelesen (Anzahl: {}), es dauerte {} ms",
                    bkrxstpCsv, bkrxstp.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
            start = LocalDateTime.now();
            erledigteBestellkarten = bkrxstp.getRows()
                    .parallelStream()
                    .map(bkrxstpRow -> erledigteBestellkarte(
                            bkrxstp.getValue(bkrxstpRow, "BEXLNR"),
                            bkrxstp.getValue(bkrxstpRow, "BEXTIT"),
                            bkrxstp.getValue(bkrxstpRow, "BEXDAT")))
                    .collect(Collectors.groupingBy(ErledigteBestellkarte::getHoerernummer,
                            Collectors.mapping(o -> o, Collectors.toList())));
            LOGGER.info("Erledigte Bestellkarten erzeugt ({} BKRXSTP -> {} Hörer), es dauerte {} ms",
                    bkrxstp.size(), erledigteBestellkarten.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    List<ErledigteBestellkarte> erledigteBestellkartenFuer(final Hoerernummer hoerernummer) {
        return erledigteBestellkarten.get(hoerernummer);
    }

}
