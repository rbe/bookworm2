/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

public final class RestServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceClient.class);

    private static final String XML_ERGIBT = "XML {} ist DlsAntwort Typ {}";

    private RestServiceClient() {
        throw new AssertionError();
    }

    private static Class<? extends DlsAntwort> errateAntworttyp(final byte[] bytes) {
        final String xml = new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
        if (xml.contains("response")) {
            LOGGER.trace(XML_ERGIBT, xml, DlsResponse.class);
            return DlsResponse.class;
        } else if (xml.contains("books")) {
            LOGGER.trace(XML_ERGIBT, xml, DlsWerke.class);
            return DlsWerke.class;
        } else if (xml.contains("book")) {
            LOGGER.trace(XML_ERGIBT, xml, DlsBook.class);
            return DlsBook.class;
        } else if (xml.contains("fehler")) {
            LOGGER.trace(XML_ERGIBT, xml, DlsFehlermeldung.class);
            return DlsFehlermeldung.class;
        } else {
            throw new IllegalStateException("Unknown message type");
        }
    }

    /*package-private für Test*/static byte[] blistaReparieren(final byte[] antwort) {
        final String xml = new String(antwort, 0, antwort.length, StandardCharsets.UTF_8);
        return xml.replace(" & ", " &amp; ")
                .getBytes(StandardCharsets.UTF_8);
    }

    public static DlsAntwort werteAntwortAus(final byte[] antwort) {
        Objects.requireNonNull(antwort);
        try {
            final Class<? extends DlsAntwort> valueType = errateAntworttyp(antwort);
            LOGGER.trace("valueType={}", valueType);
            final XmlMapper xmlMapper = new XmlMapper();
            final XMLInputFactory factory = XMLInputFactory.newFactory();
            final XMLStreamReader xmlReader = factory.createXMLStreamReader(
                    new ByteArrayInputStream(antwort));
            return xmlMapper.readValue(xmlReader, valueType);
        } catch (IOException | XMLStreamException e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public static byte[] download(final char[] username, final char[] password,
                                  final URL url) {
        try {
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(url.toURI())
                    .timeout(Duration.ofMillis(RestServiceHttpConfig.READ_TIMEOUT))
                    .header("bibliothek", String.valueOf(username))
                    .header("bibkennwort", String.valueOf(password))
                    .header("Accept", "text/xml;charset=UTF-8")
                    .build();
            return HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(RestServiceHttpConfig.CONNECT_TIMEOUT))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
                    .body();
        } catch (URISyntaxException e) {
            LOGGER.error("URL {} falsch: {}", url, e);
        } catch(IOException e){
            LOGGER.error("", e);
        } catch (InterruptedException e) {
            LOGGER.error("Unterbrochen, während URL {}: {}", url, e);
            Thread.currentThread().interrupt();
        }
        return new byte[0];
    }

}
