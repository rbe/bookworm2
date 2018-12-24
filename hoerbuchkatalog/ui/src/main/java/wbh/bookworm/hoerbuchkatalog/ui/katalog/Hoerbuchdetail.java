/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;

@Component
@RequestScope
public class Hoerbuchdetail implements Serializable {

    private final HoererSession hoererSession;

    @Autowired
    public Hoerbuchdetail(final HoererSession hoererSession) {
        this.hoererSession = hoererSession;
    }

    void leeren() {
        hoererSession.hoerbuchVergessen();
    }

    public boolean isHoerbuchVorhanden() {
        return null != hoererSession.hoerbuch();
    }

    public Sachgebiet getSachgebiet() {
        return hoererSession.hoerbuch().getSachgebiet();
    }

    public Titelnummer getTitelnummer() {
        return hoererSession.hoerbuch().getTitelnummer();
    }

    public String getAutor() {
        return hoererSession.hoerbuch().getAutor();
    }

    public String getTitel() {
        return hoererSession.hoerbuch().getTitel();
    }

    public String getUntertitel() {
        return hoererSession.hoerbuch().getUntertitel();
    }

    public String getErlaeuterung() {
        return hoererSession.hoerbuch().getErlaeuterung();
    }

    public String getVerlagsort() {
        return hoererSession.hoerbuch().getVerlagsort();
    }

    public String getVerlag() {
        return hoererSession.hoerbuch().getVerlag();
    }

    public String getDruckjahr() {
        return hoererSession.hoerbuch().getDruckjahr();
    }

    public String getSprecher() {
        return hoererSession.hoerbuch().getSprecher();
    }

    public String getSpieldauer() {
        return hoererSession.hoerbuch().getSpieldauer();
    }

    public String getProdOrt() {
        return hoererSession.hoerbuch().getProdOrt();
    }

    public String getProdJahr() {
        return hoererSession.hoerbuch().getProdJahr();
    }

    public String getAnzahlCD() {
        return hoererSession.hoerbuch().getAnzahlCD();
    }

    public String getTitelfamilie() {
        return hoererSession.hoerbuch().getTitelfamilie();
    }

    public String getEinstelldatum() {
        return hoererSession.hoerbuch().getEinstelldatumAufDeutsch();
    }

}
