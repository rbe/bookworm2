/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

class NccReader {

    private final Document document;

    NccReader(final InputStream nccHtml) {
        try {
            document = Jsoup.parse(nccHtml, "UTF-8", "");
        } catch (IOException e) {
            throw new NccReaderException(e);
        }
        if (null == document) {
            throw new NccReaderException("No document from stream");
        }
    }

    String get(final String metaName) {
        final Elements elementsByAttribute = document.head()
                .getElementsByAttributeValue("name", metaName);
        if (null != elementsByAttribute) {
            return elementsByAttribute.get(0).attr("content");
        }
        return "";
    }

    String get(final Field field) {
        return get(field.metaName);
    }

    enum Field {
        /**
         * dc:format, z.B. "DAISY 2.02".
         */
        FORMAT("dc:format"),
        /**
         * dc:title, Titel des Hörbuchs.
         */
        TITLE("dc:title"),
        /**
         * dc:publisher, Autor.
         */
        PUBLISHER("dc:publisher"),
        /**
         * dc:date, Datum.
         */
        DATE("dc:date"),
        /**
         * dc:language, Sprache.
         */
        LANGUAGE("dc:language"),
        /**
         * dc:source, Quelle; Prefix ISBN-.
         */
        SOURCE("dc:source"),
        /**
         * dc:creator, Autor.
         */
        CREATOR("dc:creator"),
        /**
         * ncc:generator, Software zur Erstellung.
         */
        GENERATOR("ncc:generator"),
        /**
         * ncc:tocItems, Anzahl der Tracks im Hörbuch.
         */
        TOC_ITEMS("ncc:tocItems"),
        /**
         * ncc:totalTime, Gesamtlänge des Hörbuchs, Format hh:mm:ss.
         */
        TOTAL_TIME("ncc:totalTime"),
        /**
         * ncc:setInfo, Anzahl Medien/CDs.
         */
        SET_INFO("ncc:setInfo"),
        /**
         * ncc:sourceDate, Jahr der Veröffentlichung.
         */
        SOURCE_DATE("ncc:sourceDate"),
        /**
         * ncc:sourcePublisher, Veröffentlicher der Quelle.
         */
        SOURCE_PUBLISHER("ncc:sourcePublisher"),
        /**
         * ncc:narrator, Erzähler/Sprecher.
         */
        NARRATOR("ncc:narrator"),
        /**
         * ncc:kByteSize, Gesamtgröße des DAISY-Buchs.
         */
        SIZE_IN_KBYTE("ncc:kByteSize"),
        /**
         * ncc:files, Anzahl Dateien in diesem DAISY-Buch.
         */
        NUM_FILES("ncc:files"),
        /**
         * prod:localID, &lt;Nummer Hörbücherei&gt;_&lt;Titelnummer&gt;, z.B. "06_32909"
         */
        LOCALID("prod:localID"),
        /**
         * prod:medibusOK, ?.
         */
        MEDIBUS_OK("prod:medibusOK"),
        /**
         * prod:audioformat, z.B. "wave 22 kHz".
         */
        AUDIOFORMAT("prod:audioformat"),
        /**
         * prod:compression, z.B. "mp3 64 kb/s".
         */
        COMPRESSION("prod:compression");

        private String metaName;

        Field(final String metaName) {
            this.metaName = metaName;
        }

        public String getMetaName() {
            return metaName;
        }

    }

}
