/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SuchparameterTest {

    @Test
    void should() {
        final Suchparameter sp = new Suchparameter();
        sp.hinzufuegen(Suchparameter.Feld.SACHGEBIET, "Z");
        sp.hinzufuegen(Suchparameter.Feld.TITEL, "Das Kapital");
        sp.hinzufuegen(Suchparameter.Feld.TITEL, "Das Kapital - II");
        assertEquals("Sachgebiet \"Z\", Titel \"Das Kapital - II\"", sp.getLabel());
    }

}
