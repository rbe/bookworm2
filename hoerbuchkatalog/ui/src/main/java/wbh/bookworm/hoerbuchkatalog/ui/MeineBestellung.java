/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class MeineBestellung {

    private Hoerernummer hoerernummer;

    private String hoerername;

    private String hoereremail;

    private String bemerkung;

    private Boolean bestellkarteMischen;

    private Boolean alteBestellkarteLoeschen;

    private final Navigation navigation;

    private final MeinWarenkorb meinWarenkorb;

    private final BestellungService bestellungService;

    private String statusNachricht;

    @Autowired
    public MeineBestellung(final Hoerernummer hoerernummer,
                           final Navigation navigation,
                           final MeinWarenkorb meinWarenkorb,
                           final BestellungService bestellungService) {
        this.hoerernummer = hoerernummer;
        this.navigation = navigation;
        this.meinWarenkorb = meinWarenkorb;
        this.bestellungService = bestellungService;
    }

    public int getAnzahl() {
        return meinWarenkorb.getTotaleAnzahl();
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public void setHoerernummer(final Hoerernummer hoerernummer) {
        if (Hoerernummer.UNBEKANNT == hoerernummer) {
            this.hoerernummer = hoerernummer;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getHoerername() {
        return hoerername;
    }

    public void setHoerername(final String hoerername) {
        this.hoerername = hoerername;
    }

    public String getHoereremail() {
        return hoereremail;
    }

    public void setHoereremail(final String hoereremail) {
        this.hoereremail = hoereremail;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(final String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Boolean getBestellkarteMischen() {
        return bestellkarteMischen;
    }

    public void setBestellkarteMischen(final Boolean bestellkarteMischen) {
        this.bestellkarteMischen = bestellkarteMischen;
    }

    public Boolean getAlteBestellkarteLoeschen() {
        return alteBestellkarteLoeschen;
    }

    public void setAlteBestellkarteLoeschen(final Boolean alteBestellkarteLoeschen) {
        this.alteBestellkarteLoeschen = alteBestellkarteLoeschen;
    }

    public String bestellungAbschicken() {
        statusNachricht = "Ihre Bestellung wird bearbeitet!";
        bestellungService.bestellungAufgeben(
                hoerernummer, hoerername, hoereremail,
                bemerkung, bestellkarteMischen, alteBestellkarteLoeschen,
                meinWarenkorb.getCd().getDomainId(), meinWarenkorb.getDownload().getDomainId());
        return navigation.zuBestellungErfolgreich();
    }

    public String getStatusNachricht() {
        return statusNachricht;
    }

}
