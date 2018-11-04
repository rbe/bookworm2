/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import wbh.bookworm.platform.ddd.model.DomainValueObject;

public class Hoerername extends DomainValueObject {

    private final Vorname vorname;

    private final Nachname nachname;

    public Hoerername(final Vorname vorname, final Nachname nachname) {
        this.vorname = vorname;
        this.nachname = nachname;
    }

    public Vorname getVorname() {
        return vorname;
    }

    public Nachname getNachname() {
        return nachname;
    }

}
