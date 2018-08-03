/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;

import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractWarenkorb {

    private Set<Hoerbuch> warenkorb;

    protected AbstractWarenkorb() {
        warenkorb = new TreeSet<>(Hoerbuch::compareTo);
    }

    public boolean enthalten(final Hoerbuch hoerbuch) {
        return warenkorb.contains(hoerbuch);
    }

    public void hinzufuegen(final Hoerbuch hoerbuch) {
        warenkorb.add(hoerbuch);
    }

    public void entfernen(final Hoerbuch hoerbuch) {
        warenkorb.remove(hoerbuch);
    }

    public int getAnzahl() {
        return warenkorb.size();
    }

}
