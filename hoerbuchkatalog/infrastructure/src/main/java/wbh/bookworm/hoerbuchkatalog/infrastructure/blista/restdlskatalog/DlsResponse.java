/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "java:S1104"})
public class DlsResponse extends DlsAntwort {

    public static class Response {

        @JsonDeserialize(using = TrimmingStringDeserializer.class)
        public String success;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public List<Fehler> fehlerliste;

    }

    @JsonDeserialize(using = TrimmingStringDeserializer.class)
    public String version;

    public Response response;

    @Override
    public boolean hatFehler() {
        return null != response.fehlerliste && !response.fehlerliste.isEmpty();
    }

    public boolean isSuccess() {
        return !"0".equals(response.success);
    }

}
