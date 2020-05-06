/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Component
@RequestScope
public class Hoerbuchdetail implements Serializable {

    private final HoererSession hoererSession;

    @Autowired
    public Hoerbuchdetail(final HoererSession hoererSession) {
        this.hoererSession = hoererSession;
    }

    void leeren() {
        hoererSession.gemerktesHoerbuchVergessen();
    }

    public boolean isHoerbuchVorhanden() {
        return null != hoererSession.gemerktesHoerbuch();
    }

    public String getSachgebiet() {
        return hoererSession.gemerktesHoerbuch().getSachgebiet().getLabel();
    }

    public Titelnummer getTitelnummer() {
        return hoererSession.gemerktesHoerbuch().getTitelnummer();
    }

    public String getAutor() {
        return hoererSession.gemerktesHoerbuch().getAutor();
    }

    public String getTitel() {
        return hoererSession.gemerktesHoerbuch().getTitel();
    }

    public String getUntertitel() {
        return hoererSession.gemerktesHoerbuch().getUntertitel();
    }

    public String getErlaeuterung() {
        return hoererSession.gemerktesHoerbuch().getErlaeuterung();
    }

    public String getVerlagsort() {
        return hoererSession.gemerktesHoerbuch().getVerlagsort();
    }

    public String getVerlag() {
        return hoererSession.gemerktesHoerbuch().getVerlag();
    }

    public String getDruckjahr() {
        return hoererSession.gemerktesHoerbuch().getDruckjahr();
    }

    public String getSprecher() {
        return hoererSession.gemerktesHoerbuch().getSprecher();
    }

    public String getSpieldauer() {
        return hoererSession.gemerktesHoerbuch().getSpieldauer();
    }

    public String getProdOrt() {
        return hoererSession.gemerktesHoerbuch().getProdOrt();
    }

    public String getProdJahr() {
        return hoererSession.gemerktesHoerbuch().getProdJahr();
    }

    public String getAnzahlCD() {
        return hoererSession.gemerktesHoerbuch().getAnzahlCD();
    }

    public String getTitelfamilie() {
        return hoererSession.gemerktesHoerbuch().getTitelfamilie();
    }

    public String getEinstelldatum() {
        return hoererSession.gemerktesHoerbuch().getEinstelldatumAufDeutsch();
    }

}
