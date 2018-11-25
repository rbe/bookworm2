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
@XmlAccessorType(XmlAccessType.FIELD)
final class Billet {

    private static final long serialVersionUID = -1L;

    @XmlElement(name = "UserID")
    private String userId;

    @XmlElement(name = "BibliothekID")
    private String bibliothekId = "wbh06";

    @XmlElement(name = "Bestellnummer")
    private String bestellnummer;

    @XmlElement(name = "Abrufkennwort")
    private String abrufkennwort;

    /**
     * @param userId ID of user requesting a download.
     * @param bestellnummer Bestellnummer == AGH-Nummer.
     * @return {@link Billet}.
     */
    static Billet create(final String userId, final String bestellnummer) {
        return new Billet(userId, bestellnummer, RandomStringGenerator.next());
    }

    Billet() {}

    private Billet(final String userId, final String bestellnummer, final String abrufkennwort) {
        this.userId = userId;
        this.bestellnummer = bestellnummer;
        this.abrufkennwort = abrufkennwort;
    }

    String getUserId() {
        return userId;
    }

/*
    void setUserId(final String userId) {
        this.userId = userId;
    }

    String getBibliothekId() {
        return bibliothekId;
    }

    void setBibliothekId(final String bibliothekId) {
        this.bibliothekId = bibliothekId;
    }
*/

    String getAghNummer() {
        return bestellnummer;
    }

/*
    void setBestellnummer(final String bestellnummer) {
        this.bestellnummer = bestellnummer;
    }
*/

    String getAbrufkennwort() {
        return abrufkennwort;
    }

/*
    void setAbrufkennwort(final String abrufkennwort) {
        this.abrufkennwort = abrufkennwort;
    }
*/

    @Override
    public String toString() {
        return String.format("Billet{userId='%s', bestellnummer='%s', abrufkennwort='%s'}", userId, bestellnummer, abrufkennwort);
    }

}
