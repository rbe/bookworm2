/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDlsDownload;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.VerfuegbareDownloads;

import aoc.ddd.repository.DomainRespositoryComponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Blista DLS
 * Agiert als ein ACL für Bestellungen -> domain.VerfuegbareDownloads/BlistaDlsDownload
 */
@DomainRespositoryComponent
public class DownloadsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsRepository.class);

    private static final int PUSHBACK_BUFFER_SIZE = 80;

    private static final String DLS_KATALOG_URL = "https://rest-dls-katalog.blista.de:443/v1/werke";

    private static final String BIBLIOTHEK = "wbh06";

    private static final String BIBKENNWORT = "qWr!by5FbaPzb8XaQ";

    private static class TrimmingStringDeserializer extends FromStringDeserializer<String> {

        private static final long serialVersionUID = 1L;

        protected TrimmingStringDeserializer() {
            super(null);
        }

        @Override
        protected String _deserialize(final String value, final DeserializationContext ctxt) {
            return value;
        }

    }

    private static class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

        private static final long serialVersionUID = 1L;

        protected LocalDateTimeDeserializer() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final String text = jp.readValueAs(String.class).trim();
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

    }

    abstract static class DlsAntwort {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public DlsFehlermeldung dlsFehlermeldung;

        public boolean hatFehler() {
            return null != dlsFehlermeldung;
        }

    }

    public static class DlsFehlermeldung extends DlsAntwort {

        static class Fehler {

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String fehlercode;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String fehlermeldung;

            @Override
            public String toString() {
                return String.format("Fehler{fehlercode='%s', fehlermeldung='%s'}", fehlercode, fehlermeldung);
            }

        }

        @JsonDeserialize(using = TrimmingStringDeserializer.class)
        public String version;

        public Fehler fehler;

        @Override
        public String toString() {
            return String.format("DlsFehlermeldung{version='%s', fehler=%s}", version, fehler);
        }

    }

    public static class DlsWerke extends DlsAntwort {

        static class Book {

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String DlsID;

            @JsonProperty("Bestellnummer")
            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Aghnummer; // AGH-Nummer

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Title;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Kundennummer;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Ausleihstatus;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Bestelldatum;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Rueckgabedatum;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String MedienFormat;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Typ;

        }

        @JsonDeserialize(using = TrimmingStringDeserializer.class)
        public String version;

        public List<Book> books;

    }

    public static class DlsBestellung extends DlsAntwort {

        static class Book {

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String DlsID;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Bestellnummer; // AGH-Nummer

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Kundennummer; // UserID

            public int Ausleihstatus;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Streampath;

            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            public LocalDateTime Bestelldatum;

            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            public LocalDateTime Rueckgabedatum;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Title;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String DlsDescription;

            public int Progress;

            public int MaxProgress;

            public int DownloadCount;

            public int MaxDownload;

            public int Gesperrt;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String DownloadLink;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String MedienFormat;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String DownloadServer;

            @JsonDeserialize(using = TrimmingStringDeserializer.class)
            public String Typ;

        }

        @JsonDeserialize(using = TrimmingStringDeserializer.class)
        public String version;

        public Book book;

    }

    private Class<? extends DlsAntwort>
    errateAntworttyp(final PushbackInputStream inputStream) throws IOException {
        final byte[] b = new byte[PUSHBACK_BUFFER_SIZE];
        int read = inputStream.read(b, 0, b.length);
        if (PUSHBACK_BUFFER_SIZE != read) {
            throw new IllegalStateException();
        }
        final String xml = new String(b, 0, b.length, StandardCharsets.UTF_8);
        inputStream.unread(b, 0, b.length);
        if (xml.contains("fehler")) {
            LOGGER.trace("XML={} ergibt {}", xml, DlsFehlermeldung.class);
            return DlsFehlermeldung.class;
        } else if (xml.contains("books")) {
            LOGGER.trace("XML={} ergibt {}", xml, DlsWerke.class);
            return DlsWerke.class;
        } else if (xml.contains("book")) {
            LOGGER.trace("XML={} ergibt {}", xml, DlsBestellung.class);
            return DlsBestellung.class;
        } else {
            throw new IllegalStateException("Unknown message type");
        }
    }

    private DlsAntwort werteAntwortAus(final InputStream inputStream) {
        try (final PushbackInputStream pbis =
                     new PushbackInputStream(inputStream, PUSHBACK_BUFFER_SIZE)) {
            final Class<? extends DlsAntwort> valueType = errateAntworttyp(pbis);
            LOGGER.trace("valueType={}", valueType);
            final XmlMapper xmlMapper = new XmlMapper();
            final XMLInputFactory factory = XMLInputFactory.newFactory();
            final XMLStreamReader xmlReader = factory.createXMLStreamReader(pbis);
            return xmlMapper.readValue(xmlReader, valueType);
        } catch (IOException | XMLStreamException e) {
            LOGGER.error("", e);
            return null;
        }
    }

    private Path download(final Hoerernummer hoerernummer,
                          final String typ, final URL url) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.addRequestProperty("bibliothek", BIBLIOTHEK);
        connection.addRequestProperty("bibkennwort", BIBKENNWORT);
        connection.addRequestProperty("Accept", "text/xml;charset=UTF-8");
        final String filename = String.format("%s-%s-%s.xml", hoerernummer, typ,
                LocalDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .replaceAll("[.]", "-")
                        .replaceAll(":", "-"));
        Files.copy(connection.getInputStream(), Path.of(filename), StandardCopyOption.REPLACE_EXISTING);
        return Path.of(filename);
    }

    Optional<DlsWerke> werke(final Hoerernummer hoerernummer) {
        try {
            final URL url = new URL(String.format("%s/%s", DLS_KATALOG_URL,
                    hoerernummer.getValue()));
            final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(download(hoerernummer, "werke", url)));
            if (dlsAntwort instanceof DlsWerke) {
                return Optional.of((DlsWerke) dlsAntwort);
            } else if (dlsAntwort instanceof DlsFehlermeldung) {
                final DlsWerke dlsWerke = new DlsWerke();
                dlsWerke.dlsFehlermeldung = (DlsFehlermeldung) dlsAntwort;
                return Optional.of(dlsWerke);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    Optional<DlsBestellung> bestellung(final Hoerernummer hoerernummer, final AghNummer aghNummer) {
        final long startBook = System.nanoTime();
        try {
            final URL url = new URL(String.format("%s/%s/%s", DLS_KATALOG_URL,
                    hoerernummer.getValue(), aghNummer.getValue()));
            final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(download(hoerernummer, "bestellung-" + aghNummer, url)));
            LOGGER.trace("{}: Abholen der Bestellung dauerte {} ms", Thread.currentThread().getName(),
                    (System.nanoTime() - startBook) / 1_000_000);
            if (dlsAntwort instanceof DlsBestellung) {
                return Optional.of((DlsBestellung) dlsAntwort);
            } else if (dlsAntwort instanceof DlsFehlermeldung) {
                final DlsBestellung dlsBestellung = new DlsBestellung();
                dlsBestellung.dlsFehlermeldung = (DlsFehlermeldung) dlsAntwort;
                return Optional.of(dlsBestellung);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    public VerfuegbareDownloads lieferungen(final Hoerernummer hoerernummer) {
        long startWerke = System.nanoTime();
        final Optional<DlsWerke> werke = werke(hoerernummer);
        if (werke.isPresent()) {
            final List<BlistaDlsDownload> bereitgestellteDownloads = werke.get()
                    .books.parallelStream()
                    .map(book -> {
                        final AghNummer aghNummer = new AghNummer(book.Aghnummer);
                        final Optional<DlsBestellung> bestellung = bestellung(hoerernummer, aghNummer);
                        if (bestellung.isPresent()) {
                            final DlsBestellung dlsBestellung = bestellung.get();
                            return new BlistaDlsDownload(
                                    hoerernummer, aghNummer,
                                    dlsBestellung.book.Ausleihstatus,
                                    dlsBestellung.book.Bestelldatum, dlsBestellung.book.Rueckgabedatum,
                                    dlsBestellung.book.DlsDescription,
                                    dlsBestellung.book.DownloadCount, dlsBestellung.book.MaxDownload,
                                    dlsBestellung.book.DownloadLink
                            );
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(BlistaDlsDownload::isBezugsfaehig)
                    .collect(Collectors.toList());
            LOGGER.trace("{}: Abholen aller Werke dauerte {} ms", Thread.currentThread().getName(),
                    (System.nanoTime() - startWerke) / 1_000_000);
            // TODO DownloadsArchive
            return new VerfuegbareDownloads(hoerernummer, bereitgestellteDownloads);
        } else {
            return null;
        }
    }

}
