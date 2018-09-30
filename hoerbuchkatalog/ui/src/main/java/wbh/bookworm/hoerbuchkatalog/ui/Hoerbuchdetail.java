/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
@SessionScope
public class Hoerbuchdetail {

    private final Hoerernummer hoerernummer;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private Hoerbuch hoerbuch;

    @Autowired
    public Hoerbuchdetail(final Hoerernummer hoerernummer,
                          final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerernummer = hoerernummer;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    void setTitelnummer(final Titelnummer titelnummer) {
        Objects.requireNonNull(titelnummer);
        hoerbuch = hoerbuchkatalogService.hole(hoerernummer, titelnummer);
    }

    void leeren() {
        this.hoerbuch = null;
    }

    public boolean isHoerbuchVorhanden() {
        return null != hoerbuch;
    }

    public Sachgebiet getSachgebiet() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getSachgebiet();
    }

    public Titelnummer getTitelnummer() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getTitelnummer();
    }

    public String getAutor() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getAutor();
    }

    public String getTitel() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getTitel();
    }

    public String getUntertitel() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getUntertitel();
    }

    public String getErlaeuterung() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getErlaeuterung();
    }

    public String getVerlagsort() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getVerlagsort();
    }

    public String getVerlag() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getVerlag();
    }

    public String getDruckjahr() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getDruckjahr();
    }

    public String getSprecher() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getSprecher();
    }

    public String getSpieldauer() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getSpieldauer();
    }

    public String getProdOrt() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getProdOrt();
    }

    public String getProdJahr() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getProdJahr();
    }

    public String getAnzahlCD() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getAnzahlCD();
    }

    public String getTitelfamilie() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getTitelfamilie();
    }

    public String getEinstelldatum() {
        Objects.requireNonNull(hoerbuch);
        return hoerbuch.getEinstelldatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}
