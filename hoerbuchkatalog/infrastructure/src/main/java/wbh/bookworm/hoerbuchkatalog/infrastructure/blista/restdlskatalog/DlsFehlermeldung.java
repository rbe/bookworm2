/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "java:S1104"})
public class DlsFehlermeldung extends DlsAntwort {

    @JsonDeserialize(using = TrimmingStringDeserializer.class)
    public String version;

    public Fehler fehler;

    @Override
    public String toString() {
        return String.format("DlsFehlermeldung{version='%s', fehler=%s}",
                version, fehler);
    }

}
