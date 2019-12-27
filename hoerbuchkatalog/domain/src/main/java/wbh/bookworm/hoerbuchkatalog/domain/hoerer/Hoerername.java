/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Hoerername extends DomainValueObject {

    public static final Hoerername UNBEKANNT = new Hoerername(Vorname.UNBEKANNT, Nachname.UNBEKANNT);

    private final Vorname vorname;

    private final Nachname nachname;

    @JsonCreator
    public Hoerername(final @JsonProperty("vorname") Vorname vorname,
                      final @JsonProperty("nachname") Nachname nachname) {
        Objects.requireNonNull(vorname);
        this.vorname = vorname;
        Objects.requireNonNull(nachname);
        this.nachname = nachname;
    }

    public static Hoerername of(final String str) {
        Objects.requireNonNull(str);
        final int lastSpacePos = str.lastIndexOf(' ');
        if (lastSpacePos > -1) {
            return new Hoerername(new Vorname(str.substring(0, lastSpacePos)),
                    new Nachname(str.substring(lastSpacePos + 1)));
        } else {
            return new Hoerername(new Vorname(""), new Nachname(str));
        }
    }

    public Vorname getVorname() {
        return vorname;
    }

    public Nachname getNachname() {
        return nachname;
    }

    public boolean irgendeinNameVorhanden() {
        return vorname.hasValue() || nachname.hasValue();
    }

    @Override
    public String toString() {
        return String.format("%s %s", vorname, nachname);
    }

    /* TODO Test
    public static void main(String[] args) {
        System.out.println(Hoerername.of("Tom-Fiete Emil Henri Bensmann-Petersen"));
    }
    */

}
