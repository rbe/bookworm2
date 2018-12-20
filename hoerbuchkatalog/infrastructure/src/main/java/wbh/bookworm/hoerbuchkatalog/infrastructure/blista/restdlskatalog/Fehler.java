/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck"})
public class Fehler {

    @JsonDeserialize(using = TrimmingStringDeserializer.class)
    public String fehlercode;

    @JsonDeserialize(using = TrimmingStringDeserializer.class)
    public String fehlermeldung;

    @Override
    public String toString() {
        return String.format("Fehler{fehlercode='%s', fehlermeldung='%s'}",
                fehlercode, fehlermeldung);
    }

}
