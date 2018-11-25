/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Blista DLS
 * Agiert als ein ACL für Bestellungen -> domain.BlistaDownloads/BlistaDownload
 */
@Component
public class DlsLieferung {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlsLieferung.class);

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

        public static class Book {

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

        public static class Book {

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

    private final DlsLieferungConfig dlsLieferungConfig;

    @Autowired
    public DlsLieferung(final DlsLieferungConfig dlsLieferungConfig) {
        this.dlsLieferungConfig = dlsLieferungConfig;
    }

/*
    private static final int PUSHBACK_BUFFER_SIZE = 80;

    @Deprecated
    private Class<? extends DlsAntwort>
    errateAntworttyp(final PushbackInputStream inputStream) throws IOException {
        final byte[] bytes = new byte[PUSHBACK_BUFFER_SIZE];
        int read = inputStream.read(bytes, 0, bytes.length);
        if (PUSHBACK_BUFFER_SIZE != read) {
            throw new IllegalStateException();
        }
        final Class<? extends DlsAntwort> aClass = errateAntworttyp(bytes);
        inputStream.unread(bytes, 0, bytes.length);
        return aClass;
    }

    @Deprecated
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

    @Deprecated
    private Path downloadToFile(final String hoerernummer, final String typ, final URL url) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.addRequestProperty("bibliothek", dlsLieferungConfig.getBibliothek());
        connection.addRequestProperty("bibkennwort", dlsLieferungConfig.getBibkennwort());
        connection.addRequestProperty("Accept", "text/xml;charset=UTF-8");
        final String filename = String.format("%s-%s-%s.xml", hoerernummer, typ,
                LocalDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .replaceAll("[.]", "-")
                        .replaceAll(":", "-"));
        Files.copy(connection.getInputStream(),
                dlsLieferungConfig.getBlistaDlsDirectory().resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
        connection.disconnect();
        return Path.of(filename);
    }
*/

    private Class<? extends DlsAntwort>
    errateAntworttyp(final byte[] bytes) {
        final String xml = new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
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

    DlsAntwort werteAntwortAus(final byte[] antwort) {
        try {
            final Class<? extends DlsAntwort> valueType = errateAntworttyp(antwort);
            LOGGER.trace("valueType={}", valueType);
            final XmlMapper xmlMapper = new XmlMapper();
            final XMLInputFactory factory = XMLInputFactory.newFactory();
            final XMLStreamReader xmlReader = factory.createXMLStreamReader(new ByteArrayInputStream(antwort));
            return xmlMapper.readValue(xmlReader, valueType);
        } catch (IOException | XMLStreamException e) {
            LOGGER.error("", e);
            return null;
        }
    }

    private byte[] download(final URL url) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.addRequestProperty("bibliothek", dlsLieferungConfig.getBibliothek());
        connection.addRequestProperty("bibkennwort", dlsLieferungConfig.getBibkennwort());
        connection.addRequestProperty("Accept", "text/xml;charset=UTF-8");
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        connection.getInputStream().transferTo(bytes);
        connection.disconnect();
        return bytes.toByteArray();
    }

    public Optional<DlsWerke> alleWerkeLaden(final String hoerernummer) {
        try {
            final URL url = new URL(String.format("%s/%s", dlsLieferungConfig.getBlistaDlsRestUrl(),
                    hoerernummer));
            /* TODO Archivieren? final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(downloadToFile(hoerernummer, "werke", url)));*/
            final DlsAntwort dlsAntwort = werteAntwortAus(download(url));
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

    public Optional<DlsBestellung> bestellungLaden(final String hoerernummer, final String aghNummer) {
        final long startBook = System.nanoTime();
        try {
            final URL url = new URL(String.format("%s/%s/%s", dlsLieferungConfig.getBlistaDlsRestUrl(),
                    hoerernummer, aghNummer));
            /* TODO Archivieren? final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(downloadToFile(hoerernummer, "bestellung-" + aghNummer, url)));*/
            final DlsAntwort dlsAntwort = werteAntwortAus(download(url));
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

}
