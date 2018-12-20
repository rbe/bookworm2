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

    private static final Auftragsquittung UNBEKANNT = new Auftragsquittung(null, null);

    private String abrufkennwort;

    private String billetStatus;

    Auftragsquittung(final String abrufkennwort, final String billetStatus) {
        this.abrufkennwort = abrufkennwort;
        this.billetStatus = billetStatus;
    }

    static Auftragsquittung unbekannt() {
        return UNBEKANNT;
    }

    //@XmlElement(name = "Abrufkennwort")
    public String getAbrufkennwort() {
        return abrufkennwort;
    }

    void setAbrufkennwort(final String abrufkennwort) {
        this.abrufkennwort = abrufkennwort;
    }

    //@XmlElement(name = "Auftragsstatus")
    public String getBilletStatus() {
        return billetStatus;
    }

    void setBilletStatus(final String billetStatus) {
        this.billetStatus = billetStatus;
    }

    @Override
    public String toString() {
        return String.format("Auftragsquittung{abrufkennwort='%s', auftragsstatus='%s'}",
                abrufkennwort, billetStatus);
    }

}
