/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;

import aoc.tools.datatransfer.CsvFormat;
import aoc.tools.datatransfer.CsvParser;
import aoc.tools.datatransfer.FileSplitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    private final CsvParser bkrxstp = new CsvParser(BKRXSTP_CSVFORMAT);

    private Map<Hoerernummer, List<ErledigteBestellkarte>> erledigteBestellkarten;

    void leseAs400Datei(final Charset csvCharset, int expectedLineCount,
                        final Path bkrxstpCsv) {
        LocalDateTime start = LocalDateTime.now();
        try {
            final Path[] split = FileSplitter.split(bkrxstpCsv, csvCharset,
                    Runtime.getRuntime().availableProcessors() / 2);
            LOGGER.debug("Lese CSV-Dateien: {}", Arrays.asList(split));
            bkrxstp.flatParseLines(csvCharset, expectedLineCount, split);
            LOGGER.info("CSV-Datei gelesen (Anzahl: {} BKRXSTP), es dauerte {} ms",
                    bkrxstp.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
            start = LocalDateTime.now();
            erledigteBestellkarten = bkrxstp.getRows()
                    .parallelStream()
                    .map(bkrxstpRow -> ErledigteBestellkarte.of(
                            bkrxstp.getValue(bkrxstpRow, "BEXLNR"),
                            bkrxstp.getValue(bkrxstpRow, "BEXTIT"),
                            bkrxstp.getValue(bkrxstpRow, "BEXDAT")))
                    //.filter(not(Objects::isNull))
                    .collect(Collectors.groupingBy(ErledigteBestellkarte::getHoerernummer,
                            Collectors.mapping(o -> o, Collectors.toList())));
            LOGGER.info("Erledigte Bestellkarten erzeugt ({} BKRXSTP -> {} erledigte Bestellkarten), es dauerte {} ms",
                    bkrxstp.size(), erledigteBestellkarten.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    public List<ErledigteBestellkarte> erledigteBestellkartenFuer(final Hoerernummer hoerernummer) {
        return erledigteBestellkarten.get(hoerernummer);
    }

}
