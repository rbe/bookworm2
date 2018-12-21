/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

    public static DlsAntwort werteAntwortAus(final byte[] antwort) {
        try {
            final Class<? extends DlsAntwort> valueType = errateAntworttyp(antwort);
            LOGGER.trace("valueType={}", valueType);
            final XmlMapper xmlMapper = new XmlMapper();
            final XMLInputFactory factory = XMLInputFactory.newFactory();
            final XMLStreamReader xmlReader =
                    factory.createXMLStreamReader(new ByteArrayInputStream(antwort));
            return xmlMapper.readValue(xmlReader, valueType);
        } catch (IOException | XMLStreamException e) {
            LOGGER.error("", e);
            return null;
        }
    }

    /* TODO Java 11 HttpClient */
    public static byte[] download(/* TODO char[] */final String username, final String password,
                                                   final URL url) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(RestServiceHttpConfig.CONNECT_TIMEOUT);
        connection.setReadTimeout(RestServiceHttpConfig.READ_TIMEOUT);
        connection.setInstanceFollowRedirects(true);
        connection.addRequestProperty("bibliothek", username);
        connection.addRequestProperty("bibkennwort", password);
        connection.addRequestProperty("Accept", "text/xml;charset=UTF-8");
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            // TODO java.net.ConnectException: Operation timed out (Connection timed out)
            connection.getInputStream().transferTo(bytes);
        } catch (SocketTimeoutException e) {
            LOGGER.error("", e);
        } finally {
            connection.disconnect();
        }
        return bytes.toByteArray();
    }

}
