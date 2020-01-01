/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "java:S116", "java:S1104"})
public class DlsBook extends DlsAntwort {

    @SuppressWarnings({"squid:S00116"})
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
