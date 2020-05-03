/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.shared.domain.Hoerernummer;

@Component
@RequestScope
public class MeineBestellung {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineBestellung.class);

    private final Navigation navigation;

    //
    // Hörer
    //

    private final HoererSession hoererSession;

    private Integer hoerernummerFormular;

    private String hoerernameAusFormular;

    private String hoereremailAusFormular;

    //
    // Bestellung
    //

    private final MeinWarenkorb meinWarenkorb;

    private final BestellungService bestellungService;

    private String bemerkung;

    private Boolean bestellkarteMischen;

    private Boolean alteBestellkarteLoeschen;

    private String bestellungStatusNachricht;

    @Autowired
    public MeineBestellung(final HoererSession hoererSession,
                           final Navigation navigation,
                           final MeinWarenkorb meinWarenkorb,
                           final BestellungService bestellungService) {
        LOGGER.trace("Initialisiere {}", hoererSession);
        this.hoererSession = hoererSession;
        this.navigation = navigation;
        this.meinWarenkorb = meinWarenkorb;
        this.bestellungService = bestellungService;
    }

    public boolean hasHoerernummer() {
        return hoererSession.isHoererIstBekannt();
    }

    public Integer getHoerernummer() {
        return hoererSession.isHoererIstBekannt()
                ? Integer.valueOf(hoererSession.getHoerernummer().getValue())
                : hoerernummerFormular;
    }

    public void setHoerernummer(final Integer hoerernummerAusFormular) {
        if (hoererSession.isHoererIstUnbekannt() || !hasHoerernummer()) {
            this.hoerernummerFormular = hoerernummerAusFormular;
            LOGGER.debug("Im Formular eingegebene Hörernummer: {}", hoerernummerAusFormular);
        }
    }

    public boolean hasHoerername() {
        return hoererSession.isHoererIstBekannt() && hoererSession.hasHoerername();
    }

    public String getHoerername() {
        return hasHoerername()
                ? hoererSession.getHoerername().toString()
                : hoerernameAusFormular;
    }

    public void setHoerername(final String hoerernameAusFormular) {
        if (hoererSession.isHoererIstUnbekannt() || !hasHoerername()) {
            this.hoerernameAusFormular = hoerernameAusFormular;
            LOGGER.debug("Im Formular eingegebener Hörername: {}", hoerernameAusFormular);
        }
    }

    public boolean hasHoereremail() {
        /* TODO */return hoererSession.hasHoereremail();
    }

    public String getHoereremail() {
        return null != hoereremailAusFormular
                ? hoereremailAusFormular
                : hoererSession.getHoereremail().toString();
        /*return hasHoereremail()
                ? hoererSession.getHoereremail().toString()
                : hoereremailAusFormular;*/
    }

    public void setHoereremail(final String hoereremailAusFormular) {
        this.hoereremailAusFormular = hoereremailAusFormular;
    }

    //
    // Bestellung
    //

    public int getAnzahlImWarenkorb() {
        LOGGER.trace("");
        return meinWarenkorb.getAnzahlGesamt();
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

    public String bestellen() {
        bestellungStatusNachricht = "Ihre Bestellung wird bearbeitet!";
        /* TODO Event? */
        hoererSession.bestellteWarenkoerbeMerken();
        final Optional<BestellungId> bestellungId = bestellungAufgeben();
        if (bestellungId.isPresent()) {
            /* TODO BestellungAufgegebenEvent meinWarenkorb.bestellungAufgegeben();*/
            /* TODO BestellungAufgegebenEvent meineDownloads.bestellungAufgegeben();*/
            bestellungStatusNachricht = "Ihre Bestellung wurde erfolgreich bearbeitet!";
            LOGGER.info("Bestellung {} für Hörer {} wurde erfolgreich aufgegeben",
                    bestellungId.get(), getHoerernummer());
            return navigation.zuBestellungErfolgreich();
        } else {
            /* TODO Event? */
            hoererSession.bestellteWarenkoerbeVergessen();
            bestellungStatusNachricht = "Ihre Bestellung konnte leider nicht bearbeitet werden," +
                    " bitte wenden Sie sich an die WBH.";
            LOGGER.error("Eine Bestellung für Hörer {} konnte nicht aufgegeben werden",
                    getHoerernummer());
            return null;
        }
    }

    private Optional<BestellungId> bestellungAufgeben() {
        return bestellungService.bestellungAufgeben(hoererSession.getBestellungSessionId(),
                new Hoerernummer(getHoerernummer()),
                Hoerername.of(getHoerername()), new HoererEmail(getHoereremail()),
                bemerkung, bestellkarteMischen, alteBestellkarteLoeschen);
    }

    public String getBestellungStatusNachricht() {
        return bestellungStatusNachricht;
    }

    public int getBestellteAnzahl() {
        LOGGER.trace("");
        return hoererSession.anzahlBestellterHoerbuecher();
    }

    public CdWarenkorb getBestellterCdWarenkorb() {
        return hoererSession.getBestellterCdWarenkorb();
    }

    public DownloadWarenkorb getBestellterDownloadWarenkorb() {
        return hoererSession.getBestellterDownloadWarenkorb();
    }

    public boolean isBestellungenVorhanden() {
        if (hoererSession.isHoererIstUnbekannt()) {
            return false;
        } else {
            return !hoererSession.isBlistaAbrufHatFehler()
                    && (/* TODO ELValueCache */bestellungService.anzahlBestellungen(hoererSession.getHoerernummer()) > 0
                    || !hoererSession.alleDownloads().isEmpty());
        }
    }

}
