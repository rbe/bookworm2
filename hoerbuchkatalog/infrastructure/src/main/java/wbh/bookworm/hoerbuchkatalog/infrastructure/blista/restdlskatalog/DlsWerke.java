/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "java:S1104"})
public class DlsWerke extends DlsAntwort {

    @SuppressWarnings({"squid:S00116"})
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
