/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
final class Quittung {

    private static final long serialVersionUID = -1L;

    private String abrufkennwort;

    private String billetStatus;

    Quittung(final String abrufkennwort, final String billetStatus) {
        this.abrufkennwort = abrufkennwort;
        this.billetStatus = billetStatus;
    }

    @XmlElement(name = "Abrufkennwort")
    String getAbrufkennwort() {
        return abrufkennwort;
    }

    void setAbrufkennwort(final String abrufkennwort) {
        this.abrufkennwort = abrufkennwort;
    }

    @XmlElement(name = "BilletStatus")
    String getBilletStatus() {
        return billetStatus;
    }

    void setBilletStatus(final String billetStatus) {
        this.billetStatus = billetStatus;
    }

    @Override
    public String toString() {
        return "Quittung{" +
                "abrufkennwort='" + abrufkennwort + '\'' +
                ", billetStatus='" + billetStatus + '\'' +
                '}';
    }

}
