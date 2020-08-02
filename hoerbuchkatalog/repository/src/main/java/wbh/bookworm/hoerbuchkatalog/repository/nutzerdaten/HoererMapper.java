/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.hoerbuchkatalog.repository.as400.Datenformat;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

import aoc.mikrokosmos.io.dataformat.CsvFormat;
import aoc.mikrokosmos.io.dataformat.CsvParser;
import aoc.mikrokosmos.io.dataformat.Executor;

import static java.util.function.Predicate.not;

@Component
@Lazy
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings({"squid:S1192", "java:S1192"})
final class HoererMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererMapper.class);

    private static final CsvFormat HOERSTP_CSVFORMAT = new CsvFormat();

    static {
        HOERSTP_CSVFORMAT.addField("HOENR", "HOERER NR");
        HOERSTP_CSVFORMAT.addField("HOEAN", "ANREDE");
        HOERSTP_CSVFORMAT.addField("HOENN", "NACHNAME");
        HOERSTP_CSVFORMAT.addField("HOEVN", "VORNAME");
        HOERSTP_CSVFORMAT.addField("HOEN2", "2. NAME");
        HOERSTP_CSVFORMAT.addField("HOESTR", "STRAßE");
        HOERSTP_CSVFORMAT.addField("HOEPLL", "LAND VOR PLZ");
        HOERSTP_CSVFORMAT.addField("HOEPLZ", "PLZ");
        HOERSTP_CSVFORMAT.addField("HOEORT", "ORT");
        HOERSTP_CSVFORMAT.addField("HOENPB", "NÄHERE POST.BEZ");
        HOERSTP_CSVFORMAT.addField("HOEGBD", "GEBURTSDATUM");
        HOERSTP_CSVFORMAT.addField("HOECC9", "Boxen/CDs");
        HOERSTP_CSVFORMAT.addField("HOEGES", "GESCHLECHT");
        HOERSTP_CSVFORMAT.addField("HOEHKZ", "HÖRERKZ");
        HOERSTP_CSVFORMAT.addField("HOEANM", "ANMELDUNG");
        HOERSTP_CSVFORMAT.addField("HOEMD1", "MEDIEN KZ1");
        HOERSTP_CSVFORMAT.addField("HOEMD2", "MEDIEN KZ2");
        HOERSTP_CSVFORMAT.addField("HOEMD3", "MEDIEN KZ3");
        HOERSTP_CSVFORMAT.addField("HOEMD4", "MEDIEN KZ4");
        HOERSTP_CSVFORMAT.addField("HOEMD5", "MEDIEN KZ5");
        HOERSTP_CSVFORMAT.addField("HOELZ", "LAND-KZ");
        HOERSTP_CSVFORMAT.addField("HOETV", "TERMIN VON DATUM");
        HOERSTP_CSVFORMAT.addField("HOETB", "TERMIN BIS DATUM");
        HOERSTP_CSVFORMAT.addField("HOEUKZ", "KENNZ.URLAUB");
        HOERSTP_CSVFORMAT.addField("HOEUV", "URLAUB VON DATUM");
        HOERSTP_CSVFORMAT.addField("HOEUB", "URLAUB BIS DATUM");
        HOERSTP_CSVFORMAT.addField("HOEVMK", "VERSAND KZ NORMAL");
        HOERSTP_CSVFORMAT.addField("HOEVUK", "VERSAND KZ URLAUB");
        HOERSTP_CSVFORMAT.addField("HOEVRK", "VERSAND KZ RECHN");
        HOERSTP_CSVFORMAT.addField("HOUN2", "2. NAME");
        HOERSTP_CSVFORMAT.addField("HOUSTR", "STRAßE");
        HOERSTP_CSVFORMAT.addField("HOUPLL", "LAND VOR PLZ");
        HOERSTP_CSVFORMAT.addField("HOUPLZ", "PLZ");
        HOERSTP_CSVFORMAT.addField("HOUORT", "ORT");
        HOERSTP_CSVFORMAT.addField("HOUNPB", "NÄHERE POST.BEZ");
        HOERSTP_CSVFORMAT.addField("HORAN", "ANREDE");
        HOERSTP_CSVFORMAT.addField("HORNN", "NACHNAME");
        HOERSTP_CSVFORMAT.addField("HORVN", "VORNAME");
        HOERSTP_CSVFORMAT.addField("HORN2", "2. NAME");
        HOERSTP_CSVFORMAT.addField("HORSTR", "STRAßE");
        HOERSTP_CSVFORMAT.addField("HORPLL", "LAND VOR PLZ");
        HOERSTP_CSVFORMAT.addField("HORPLZ", "PLZ");
        HOERSTP_CSVFORMAT.addField("HORORT", "ORT");
        HOERSTP_CSVFORMAT.addField("HORNPB", "NÄHERE POST.BEZ");
        HOERSTP_CSVFORMAT.addField("HOERV", "URLAUB VON DATUM");
        HOERSTP_CSVFORMAT.addField("HOERB", "URLAUB BIS DATUM");
        HOERSTP_CSVFORMAT.addField("HOEAA", "KOMMENTAR");
        HOERSTP_CSVFORMAT.addField("HOESPV", "SPERR VER");
        HOERSTP_CSVFORMAT.addField("HOELKZ", "LOESCH KZ");
        HOERSTP_CSVFORMAT.addField("HOETEL", "TELEFONNR");
        HOERSTP_CSVFORMAT.addField("HOEMST", "MAHNSTUFE");
        HOERSTP_CSVFORMAT.addField("HOLDAT", "LÖSCH-DATUM");
    }

    private final CsvParser hoerstp = new CsvParser(HOERSTP_CSVFORMAT);

    private static final CsvFormat HOEKZSTP_CSVFORMAT = new CsvFormat();

    static {
        HOEKZSTP_CSVFORMAT.addField("HOEKZN", "HOERER NR.");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ2", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ3", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ4", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ5", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ6", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ7", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ8", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZ9", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZA", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZB", "");
        HOEKZSTP_CSVFORMAT.addField("HOEKZC", "");
        HOEKZSTP_CSVFORMAT.addField("HOELAN", "LAND AUSGESCH.");
        HOEKZSTP_CSVFORMAT.addField("HOELA2", "LAND AUSG.URL.");
        HOEKZSTP_CSVFORMAT.addField("HOELA3", "LAND AUSG.REC.");
        HOEKZSTP_CSVFORMAT.addField("HOKZ01", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ02", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ03", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ04", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ05", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ06", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ07", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ08", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ09", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ10", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ11", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ12", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ13", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ14", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ15", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ16", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ17", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ18", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ19", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ20", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ21", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ22", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ23", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ24", "FREIE FELDER");
        HOEKZSTP_CSVFORMAT.addField("HOKZ25", "FREIE FELDER");
    }

    private final CsvParser hoekzstp = new CsvParser(HOEKZSTP_CSVFORMAT);

    private static final CsvFormat HOEBSTP_CSVFORMAT = new CsvFormat();

    static {
        HOEBSTP_CSVFORMAT.addField("BUHNR", "HOERER NR.");
        HOEBSTP_CSVFORMAT.addField("BUANM", "ANMELDEDATUM");
        HOEBSTP_CSVFORMAT.addField("BUMGI", "MENGEN-INDEX");
        HOEBSTP_CSVFORMAT.addField("BUVON", "TERMIN VON");
        HOEBSTP_CSVFORMAT.addField("BUBIS", "TERMIN BIS");
        HOEBSTP_CSVFORMAT.addField("BUSPR", "SPERR KZ");
        HOEBSTP_CSVFORMAT.addField("BUBKKZ", "BESTELLK.KZ");
        HOEBSTP_CSVFORMAT.addField("BUSGB", "SACHGEBIET");
        HOEBSTP_CSVFORMAT.addField("BURKZ", "RÜCKBUCHUNGS-KZ");
        HOEBSTP_CSVFORMAT.addField("BUBEL1", "BELASTUNG 1");
        HOEBSTP_CSVFORMAT.addField("BUDAT1", "BEL.DATUM 1");
        HOEBSTP_CSVFORMAT.addField("BUBEL2", "BELASTUNG 2");
        HOEBSTP_CSVFORMAT.addField("BUDAT2", "BEL.DATUM 2");
        HOEBSTP_CSVFORMAT.addField("BUBEL3", "BELASTUNG 3");
        HOEBSTP_CSVFORMAT.addField("BUDAT3", "BEL.DATUM 3");
        HOEBSTP_CSVFORMAT.addField("BUBEL4", "BELASTUNG 4");
        HOEBSTP_CSVFORMAT.addField("BUDAT4", "BEL.DATUM 4");
        HOEBSTP_CSVFORMAT.addField("BUBEL5", "BELASTUNG 5");
        HOEBSTP_CSVFORMAT.addField("BUDAT5", "BEL.DATUM 5");
        HOEBSTP_CSVFORMAT.addField("BUBEL6", "BELASTUNG 6");
        HOEBSTP_CSVFORMAT.addField("BUDAT6", "BEL.DATUM 6");
        HOEBSTP_CSVFORMAT.addField("BUBEL7", "BELASTUNG 7");
        HOEBSTP_CSVFORMAT.addField("BUDAT7", "BEL.DATUM 7");
        HOEBSTP_CSVFORMAT.addField("BUBEL8", "BELASTUNG 8");
        HOEBSTP_CSVFORMAT.addField("BUDAT8", "BEL.DATUM 8");
        HOEBSTP_CSVFORMAT.addField("BUBEL9", "BELASTUNG 9");
        HOEBSTP_CSVFORMAT.addField("BUDAT9", "BEL.DATUM 9");
        HOEBSTP_CSVFORMAT.addField("BUBEL10", "BELASTUNG 10");
        HOEBSTP_CSVFORMAT.addField("BUDAT10", "BEL.DATUM 10");
        HOEBSTP_CSVFORMAT.addField("BUBEL11", "BELASTUNG 11");
        HOEBSTP_CSVFORMAT.addField("BUDAT11", "BEL.DATUM 11");
        HOEBSTP_CSVFORMAT.addField("BUBEL12", "BELASTUNG 12");
        HOEBSTP_CSVFORMAT.addField("BUDAT12", "BEL.DATUM 12");
        HOEBSTP_CSVFORMAT.addField("BUBEL13", "BELASTUNG 13");
        HOEBSTP_CSVFORMAT.addField("BUDAT13", "BEL.DATUM 13");
        HOEBSTP_CSVFORMAT.addField("BUBEL14", "BELASTUNG 14");
        HOEBSTP_CSVFORMAT.addField("BUDAT14", "BEL.DATUM 14");
        HOEBSTP_CSVFORMAT.addField("BUBEL15", "BELASTUNG 15");
        HOEBSTP_CSVFORMAT.addField("BUDAT15", "BEL.DATUM 15");
        HOEBSTP_CSVFORMAT.addField("BUBEL16", "BELASTUNG 16");
        HOEBSTP_CSVFORMAT.addField("BUDAT16", "BEL.DATUM 16");
        HOEBSTP_CSVFORMAT.addField("BUBEL17", "BELASTUNG 17");
        HOEBSTP_CSVFORMAT.addField("BUDAT17", "BEL.DATUM 17");
        HOEBSTP_CSVFORMAT.addField("BUBEL18", "BELASTUNG 18");
        HOEBSTP_CSVFORMAT.addField("BUDAT18", "BEL.DATUM 18");
        HOEBSTP_CSVFORMAT.addField("BUBEL19", "BELASTUNG 19");
        HOEBSTP_CSVFORMAT.addField("BUDAT19", "BEL.DATUM 19");
        HOEBSTP_CSVFORMAT.addField("BUBEL20", "BELASTUNG 20");
        HOEBSTP_CSVFORMAT.addField("BUDAT20", "BEL.DATUM 20");
        HOEBSTP_CSVFORMAT.addField("BUBEL21", "BELASTUNG 21");
        HOEBSTP_CSVFORMAT.addField("BUDAT21", "BEL.DATUM 21");
        HOEBSTP_CSVFORMAT.addField("BUBEL22", "BELASTUNG 22");
        HOEBSTP_CSVFORMAT.addField("BUDAT22", "BEL.DATUM 22");
        HOEBSTP_CSVFORMAT.addField("BUBEL23", "BELASTUNG 23");
        HOEBSTP_CSVFORMAT.addField("BUDAT23", "BEL.DATUM 23");
        HOEBSTP_CSVFORMAT.addField("BUBEL24", "BELASTUNG 24");
        HOEBSTP_CSVFORMAT.addField("BUDAT24", "BEL.DATUM 24");
        HOEBSTP_CSVFORMAT.addField("BUBEL25", "BELASTUNG 25");
        HOEBSTP_CSVFORMAT.addField("BUDAT25", "BEL.DATUM 25");
        HOEBSTP_CSVFORMAT.addField("BUBEL26", "BELASTUNG 26");
        HOEBSTP_CSVFORMAT.addField("BUDAT26", "BEL.DATUM 26");
        HOEBSTP_CSVFORMAT.addField("BBEL27", "BELASTUNG 27");
        HOEBSTP_CSVFORMAT.addField("BDAT27", "BEL.DATUM 27");
        HOEBSTP_CSVFORMAT.addField("BUKT01", "KATALOG 1");
        HOEBSTP_CSVFORMAT.addField("BUKT02", "KATALOG 2");
        HOEBSTP_CSVFORMAT.addField("BUKT03", "KATALOG 3");
        HOEBSTP_CSVFORMAT.addField("BUKT04", "KATALOG 4");
        HOEBSTP_CSVFORMAT.addField("BUKT05", "KATALOG 5");
        HOEBSTP_CSVFORMAT.addField("BUKT06", "KATALOG 6");
        HOEBSTP_CSVFORMAT.addField("BUKT07", "KATALOG 7");
        HOEBSTP_CSVFORMAT.addField("BUKT08", "KATALOG 8");
        HOEBSTP_CSVFORMAT.addField("BUKT09", "KATALOG 9");
        HOEBSTP_CSVFORMAT.addField("BUKT10", "KATALOG 10");
        HOEBSTP_CSVFORMAT.addField("BUKT11", "KATALOG 11");
        HOEBSTP_CSVFORMAT.addField("BUKT12", "KATALOG 12");
        HOEBSTP_CSVFORMAT.addField("BUKT13", "KATALOG 13");
        HOEBSTP_CSVFORMAT.addField("BUKT14", "KATALOG 14");
        HOEBSTP_CSVFORMAT.addField("BUKT15", "KATALOG 15");
        HOEBSTP_CSVFORMAT.addField("BURDAT", "RÜCK.DAT");
        HOEBSTP_CSVFORMAT.addField("BULKZ", "LÖSCH KZ");
    }

    private final CsvParser hoebstp = new CsvParser(HOEBSTP_CSVFORMAT);

    private Map<Hoerernummer, Hoerer> hoerer;

    @Autowired
    public HoererMapper(final ExecutorService executorService,
                        final HoererRepositoryConfig hoererRepositoryConfig) {
        leseAs400Dateien(executorService,
                StandardCharsets.ISO_8859_1, 9_000,
                hoererRepositoryConfig.getDirectory().resolve("hoerstp.csv"),
                hoererRepositoryConfig.getDirectory().resolve("hoekzstp.csv"),
                hoererRepositoryConfig.getDirectory().resolve("hoebstp.csv"));
    }

    /* not private; see Test */
    void leseAs400Dateien(final ExecutorService executorService,
                          final Charset csvCharset, int expectedLineCount,
                          final Path hoerstpCsv, final Path hoekzstpCsv, final Path hoebstpCsv) {
        if (Files.exists(hoerstpCsv) && Files.exists(hoekzstpCsv) && Files.exists(hoebstpCsv)) {
            LocalDateTime start = LocalDateTime.now();
            Executor.invokeAllAndGet(executorService, Arrays.asList(
                    () -> hoerstp.flatParseLines(csvCharset, expectedLineCount, hoerstpCsv),
                    () -> hoekzstp.flatParseLines(csvCharset, expectedLineCount, hoekzstpCsv),
                    () -> hoebstp.flatParseLines(csvCharset, expectedLineCount, hoebstpCsv)
            ));
            LOGGER.info("Alle CSV-Dateien gelesen (Anzahl: {} HOERSTP/{} HOEKZ/{} HOEBSTP), es dauerte {} ms",
                    hoerstp.size(), hoekzstp.size(), hoebstp.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
            start = LocalDateTime.now();
            hoerer = hoerstp.getRows()
                    .parallelStream()
                    .map(this::hoererAusAs400Dateien)
                    .filter(not(Objects::isNull))
                    .collect(Collectors.toUnmodifiableMap(Hoerer::getHoerernummer, o -> o));
            LOGGER.info("Alle CSV-Dateien miteinander verknüpft ({} HOERSTP -> {} Hörer), es dauerte {} ms",
                    hoerstp.size(), hoerer.size(),
                    Duration.between(start, LocalDateTime.now()).toMillis());
        } else {
            LOGGER.warn("CSV-Dateien {}, {}, {} nicht gefunden/lesbar", hoerstpCsv, hoekzstpCsv, hoebstpCsv);
            hoerer = Collections.emptyMap();
        }
    }

    private Hoerer hoererAusAs400Dateien(final String[] hoerstpRow) {
        final Hoerernummer hoerernummer =
                new Hoerernummer(hoerstp.getValue(hoerstpRow, "HOENR"));
        LOGGER.trace("Verheirate Daten für Hörer {}", hoerernummer);
        try {
            final String[] hoekzstpRow =
                    hoekzstp.findRowByColumnValue("HOEKZN", hoerernummer.getValue());
            final String[] hoebstpRow =
                    hoebstp.findRowByColumnValue("BUHNR", hoerernummer.getValue());
            return new Hoerer(
                    hoerernummer,
                    hoerstp.getValue(hoerstpRow, "HOEAN"),
                    new Hoerername(
                            new Vorname(hoerstp.getValue(hoerstpRow, "HOEVN")),
                            new Nachname(hoerstp.getValue(hoerstpRow, "HOENN"))),
                    hoerstp.getValue(hoerstpRow, "HOEN2"),
                    hoerstp.getValue(hoerstpRow, "HOESTR"),
                    hoerstp.getValue(hoerstpRow, "HOEPLZ"),
                    hoerstp.getValue(hoerstpRow, "HOEORT"),
                    hoerstp.getValue(hoerstpRow, "HOENPB"),
                    hoekzstp.getValue(hoekzstpRow, "HOELAN"),
                    Datenformat.localDateOf(hoerernummer.getValue(), "HOERSTP/HOETV",
                            hoerstp.getValue(hoerstpRow, "HOETV")),
                    Datenformat.localDateOf(hoerernummer.getValue(), "HOERSTP/HOETB",
                            hoerstp.getValue(hoerstpRow, "HOETB")),
                    Datenformat.localDateOf(hoerernummer.getValue(), "HOERSTP/HOEUV",
                            hoerstp.getValue(hoerstpRow, "HOEUV")),
                    Datenformat.localDateOf(hoerernummer.getValue(), "HOERSTP/HOEUB",
                            hoerstp.getValue(hoerstpRow, "HOEUB")),
                    hoerstp.getValue(hoerstpRow, "HOUN2"),
                    hoerstp.getValue(hoerstpRow, "HOUSTR"),
                    hoerstp.getValue(hoerstpRow, "HOUPLZ"),
                    hoerstp.getValue(hoerstpRow, "HOUORT"),
                    hoerstp.getValue(hoerstpRow, "HOUNPB"),
                    hoekzstp.getValue(hoekzstpRow, "HOELA2"),
                    Datenformat.localDateOf(hoerernummer.getValue(), "HOERSTP/HOEGBD",
                            hoerstp.getValue(hoerstpRow, "HOEGBD")),
                    hoerstp.getValue(hoerstpRow, "HOETEL"),
                    new HoererEmail(hoekzstp.getValue(hoekzstpRow, "HOKZ12")),
                    aoc.mikrokosmos.io.dataformat.ParseHelper.parseInt(hoebstp.getValue(hoebstpRow, "BUMGI")),
                    Datenformat.localDateOf(hoerernummer.getValue(), "HOEBSTP/BURDAT",
                            hoebstp.getValue(hoebstpRow, "BURDAT"))
            );
        } catch (Exception e) {
            LOGGER.error("Kann Daten für Hörer " + hoerernummer + " nicht verheiraten", e);
            return null;
        }
    }

    private List<Belastung> belastungen(final String[] hoebstpRow) {
        if (isEmpty(hoebstpRow)) {
            return Collections.emptyList();
        }
        final int stellenBoxnummer = 3;
        return IntStream.range(1, 27)
                .mapToObj(i -> {
                    // BUBEL1-27 == 24151410
                    //  Titelnummer ^^^^^
                    //                   ^^^ Boxnummer (immer die letzten drei)
                    final String bubel = hoebstp.getValue(hoebstpRow, "BUBEL" + i);
                    if (null != bubel && !bubel.isBlank() && !"0".equals(bubel)) {
                        final String titelnummer = bubel.substring(0, bubel.length() - stellenBoxnummer);
                        final String boxnummer = bubel.substring(bubel.length() - stellenBoxnummer);
                        // BUDAT1-27 == 20150224
                        final String budat = hoebstp.getValue(hoebstpRow, "BUDAT" + i);
                        return new Belastung(LocalDate.parse(budat, DateTimeFormatter.BASIC_ISO_DATE),
                                boxnummer, new Titelnummer(titelnummer));
                    } else {
                        return null;
                    }
                })
                .filter(not(Objects::isNull))
                .collect(Collectors.toList());
    }

    List<Belastung> belastungenFuer(final Hoerernummer hoerernummer) {
        final String[] buhnrs = hoebstp.findRowByColumnValue("BUHNR", hoerernummer.getValue());
        return isNotEmpty(buhnrs) ? belastungen(buhnrs) : Collections.emptyList();
    }

    private boolean isEmpty(final String[] arr) {
        return null == arr || arr.length == 0;
    }

    private boolean isNotEmpty(final String[] arr) {
        return null != arr && arr.length > 0;
    }

    Hoerer hoerer(final Hoerernummer hoerernummer) {
        return hoerer.getOrDefault(hoerernummer, Hoerer.UNBEKANNT);
    }

}
