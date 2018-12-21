/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import java.io.Serializable;

/*
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
*/
public final class Auftragsquittung implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String aghNummer;

    private final boolean pruefungOk;

    private boolean uebermittlungOk;

    Auftragsquittung(final String aghNummer, final boolean pruefungOk) {
        this.aghNummer = aghNummer;
        this.pruefungOk = pruefungOk;
    }

    public String getAghNummer() {
        return aghNummer;
    }

    public boolean isPruefungOk() {
        return pruefungOk;
    }

    void uebermittlungOk() {
        this.uebermittlungOk = true;
    }

    public boolean isUebermittlungOk() {
        return uebermittlungOk;
    }

    @Override
    public String toString() {
        return String.format("Auftragsquittung{aghNummer='%s', pruefungOk=%s, uebermittlungOk=%s}",
                aghNummer, pruefungOk, uebermittlungOk);
    }

}
