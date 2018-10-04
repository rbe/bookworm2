/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;

@Component
@SessionScope
public class MeineBestellung {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineBestellung.class);

    private Hoerernummer hoerernummer;

    private String hoerername;

    private String hoereremail;

    private String bemerkung;

    private Boolean bestellkarteMischen;

    private Boolean alteBestellkarteLoeschen;

    private final Navigation navigation;

    private final MeinWarenkorb meinWarenkorb;

    private final BestellungService bestellungService;

    private String bestellungStatusNachricht;

    private CdWarenkorb bestellerCdWarenkorb;

    private DownloadWarenkorb bestellerDownloadWarenkorb;

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

    /**
     * Command
     */
    public String bestellungAbschicken() {
        // Warenkörbe kopieren
        bestellerCdWarenkorb = bestellungService.cdWarenkorbKopie(hoerernummer);
        bestellerDownloadWarenkorb = bestellungService.downloadWarenkorbKopie(hoerernummer);
        // Bestellung aufgeben
        final Optional<BestellungId> bestellungId = bestellungService.bestellungAufgeben(
                hoerernummer,
                hoerername, hoereremail,
                bemerkung,
                bestellkarteMischen, alteBestellkarteLoeschen,
                meinWarenkorb.getCd().getDomainId(), meinWarenkorb.getDownload().getDomainId());
        if (bestellungId.isPresent()) {
            // Warenkörbe leeren
            meinWarenkorb.leeren();
            bestellungStatusNachricht = "Ihre Bestellung wird bearbeitet!";
            LOGGER.info("Bestellung {} für Hörer {} wurde erfolgreich aufgegeben",
                    bestellungId.get(), hoerernummer);
            return navigation.zuBestellungErfolgreich();
        } else {
            LOGGER.error("Bestellung konnte nicht aufgegeben werden!");
            bestellerCdWarenkorb = null;
            bestellerDownloadWarenkorb = null;
            bestellungStatusNachricht = "Ihre Bestellung konnte leider nicht bearbeitet werden," +
                    " bitte wenden Sie sich an die WBH.";
            return navigation.zuMeinemWarenkorb();
        }
    }

    public String getBestellungStatusNachricht() {
        return bestellungStatusNachricht;
    }

    public CdWarenkorb getBestellerCdWarenkorb() {
        return bestellerCdWarenkorb;
    }

    public DownloadWarenkorb getBestellerDownloadWarenkorb() {
        return bestellerDownloadWarenkorb;
    }

}
