/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import aoc.tools.datatransfer.CsvFormat;
import aoc.tools.datatransfer.CsvParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
final class ErledigteBestellkartenMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErledigteBestellkartenMapper.class);

    // TODO Konfiguration
    private static final int TIMEOUT_SECONDS = 60;

    private final ExecutorService executorService;

    private static final CsvFormat BKRXSTP_CSVFORMAT = new CsvFormat();
    static {
        BKRXSTP_CSVFORMAT.addField("BEXLNR", "HÖRER-NR");
        BKRXSTP_CSVFORMAT.addField("BEXTIT", "TITEL-NR");
        BKRXSTP_CSVFORMAT.addField("BEXDAT", "AUSLEIH-DATUM");
        BKRXSTP_CSVFORMAT.addField("BEXKZ", "KENNZEICHEN");
    }
    private final CsvParser bkrxstp = new CsvParser(BKRXSTP_CSVFORMAT);

    @Autowired
    public ErledigteBestellkartenMapper(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    private void leseAs400Datei(final Path bkrxstpCsv, final Charset csvCharset,
                                int expectedLineCount) {
        final LocalDateTime start = LocalDateTime.now();
        try {
            executorService.submit(() -> bkrxstp.parseLines(bkrxstpCsv, csvCharset, expectedLineCount));
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            LOGGER.debug("executorService: shutdown={}, terminated={}",
                    executorService.isShutdown(), executorService.isTerminated());
            LOGGER.info("CSV-Datei gelesen (Anzahl: {} BKRXSTP), es dauerte {} ms",
                    bkrxstp.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

}
