/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public abstract class DlsAntwort {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public DlsFehlermeldung dlsFehlermeldung;

    public boolean hatFehler() {
        return null != dlsFehlermeldung;
    }

}
