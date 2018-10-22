/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.lieferung.download;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class BlistaAbfrage {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlistaAbfrage.class);

    private static final int PUSHBACK_BUFFER_SIZE = 80;

    private static final String DLS_KATALOG_URL = "https://rest-dls-katalog.blista.de:443/v1/werke";

    private static final String BIBLIOTHEK = "wbh06";

    private static final String BIBKENNWORT = "qWr!by5FbaPzb8XaQ";

    private abstract static class DlsAntwort {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public Fehlermeldung fehlermeldung;

        public boolean hatFehler() {
            return null != fehlermeldung;
        }

    }

    private static class Fehlermeldung extends DlsAntwort {

        static class Fehler {

            public String fehlercode;

            public String fehlermeldung;

            @Override
            public String toString() {
                return String.format("Fehler{fehlercode='%s', fehlermeldung='%s'}", fehlercode, fehlermeldung);
            }

        }

        public String version;

        public Fehler fehler;

        @Override
        public String toString() {
            return String.format("Fehlermeldung{version='%s', fehler=%s}", version, fehler);
        }

    }

    private static class Werke extends DlsAntwort {

        static class Book {

            public String DlsID;

            public String Bestellnummer; // AGH-Nummer

            public String Title;

            public String Kundennummer;

            public String Ausleihstatus;

            public String Bestelldatum;

            public String Rueckgabedatum;

            public String MedienFormat;

            public String Typ;

        }

        public String version;

        public List<Book> books;

    }

    private static class Bestellung extends DlsAntwort {

        static class Book {

            public String DlsID;

            public String Bestellnummer; // AGH-Nummer

            public String Kundennummer; // UserID

            public int Ausleihstatus;

            public String Streampath;

            public String Bestelldatum;

            public String Rueckgabedatum;

            public String Title;

            public String DlsDescription;

            public int Progress;

            public int MaxProgress;

            public int DownloadCount;

            public int MaxDownload;

            public int Gesperrt;

            public String DownloadLink;

            public String MedienFormat;

            public String DownloadServer;

            public String Typ;

        }

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
            LOGGER.trace("XML={} ergibt {}", xml, Fehlermeldung.class);
            return Fehlermeldung.class;
        } else if (xml.contains("books")) {
            LOGGER.trace("XML={} ergibt {}", xml, Werke.class);
            return Werke.class;
        } else if (xml.contains("book")) {
            LOGGER.trace("XML={} ergibt {}", xml, Bestellung.class);
            return Bestellung.class;
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
        final Path file = Files.createFile(Paths.get(filename));
        Files.copy(connection.getInputStream(), file, StandardCopyOption.REPLACE_EXISTING);
        return file;
    }

    public Optional<Werke> werke(final Hoerernummer hoerernummer) {
        try {
            final URL url = new URL(String.format("%s/%s", DLS_KATALOG_URL,
                    hoerernummer.getValue()));
            final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(download(hoerernummer, "werke", url)));
            if (dlsAntwort instanceof Werke) {
                return Optional.ofNullable((Werke) dlsAntwort);
            } else if (dlsAntwort instanceof Fehlermeldung) {
                final Werke werke = new Werke();
                werke.fehlermeldung = (Fehlermeldung) dlsAntwort;
                return Optional.of(werke);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    public Optional<Bestellung> bestellung(final Hoerernummer hoerernummer, final AghNummer aghNummer) {
        try {
            final URL url = new URL(String.format("%s/%s/%s", DLS_KATALOG_URL,
                    hoerernummer.getValue(), aghNummer.getValue()));
            final DlsAntwort dlsAntwort = werteAntwortAus(
                    Files.newInputStream(download(hoerernummer, "bestellung", url)));
            if (dlsAntwort instanceof Bestellung) {
                return Optional.ofNullable((Bestellung) dlsAntwort);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
/*
        final InputStream inputStream = BlistaAbfrage.class.getResourceAsStream("/fehlermeldung.xml");
        final DlsAntwort dlsAntwort = BlistaAbfrage.werteAntwortAus(inputStream)
                .orElseThrow();
*/
        final DlsAntwort dlsAntwort = new BlistaAbfrage().werke(new Hoerernummer("711012345"))
                .orElseThrow();
/*
        final DlsAntwort dlsAntwort = BlistaAbfrage.bestellung(
                new Hoerernummer("7110"), new AghNummer("1-0081537-1-0"))
                .orElseThrow();
*/
        if (dlsAntwort instanceof Werke) {
            final Werke werke = (Werke) dlsAntwort;
            if (!werke.hatFehler()) {
                System.out.println(werke.books.size());
            } else {
                System.out.println(werke.fehlermeldung.fehler.fehlermeldung);
            }
        } else if (dlsAntwort instanceof Bestellung) {
            System.out.println(((Bestellung) dlsAntwort).book.Bestelldatum);
        }
    }

}
