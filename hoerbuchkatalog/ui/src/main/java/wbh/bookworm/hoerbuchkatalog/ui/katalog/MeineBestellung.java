/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;

import aoc.jsf.ELValueCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Optional;

@Component
@SessionScope
public class MeineBestellung implements Serializable {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(MeineBestellung.class);

    private final Navigation navigation;

    //
    // Hörer
    //

    private final HoererSession hoererSession;

    private Integer hoerernummerFormular;

    private String hoerernameAusFormular;

    private String hoereremailAusFormular;

    private final MeineDownloads meineDownloads;

    //
    // Bestellung
    //

    private final MeinWarenkorb meinWarenkorb;

    private final transient BestellungService bestellungService;

    private String bemerkung;

    private Boolean bestellkarteMischen;

    private Boolean alteBestellkarteLoeschen;

    private String bestellungStatusNachricht;

    private CdWarenkorb bestellterCdWarenkorb;

    private DownloadWarenkorb bestellterDownloadWarenkorb;

    private final transient ELValueCache<Integer> anzahlBestellterHoerbuecherValueCache;

    private final transient ELValueCache<HoererBlistaDownloads> verfuegbareDownloadsELCache;

    @Autowired
    public MeineBestellung(final Navigation navigation,
                           final HoererSession hoererSession,
                           final MeineDownloads meineDownloads,
                           final MeinWarenkorb meinWarenkorb,
                           final BestellungService bestellungService,
                           final DownloadsLieferungService downloadsLieferungService) {
        LOGGER.trace("Initialisiere für Hörer {}", hoererSession.getHoerernummer());
        this.hoererSession = hoererSession;
        this.meineDownloads = meineDownloads;
        this.navigation = navigation;
        this.meinWarenkorb = meinWarenkorb;
        this.bestellungService = bestellungService;
        anzahlBestellterHoerbuecherValueCache = new ELValueCache<>(0,
                () -> {
                    final int anzahlCDs =
                            null != bestellterCdWarenkorb
                                    ? bestellterCdWarenkorb.getAnzahl()
                                    : 0;
                    final int anzahlDownloads =
                            null != bestellterDownloadWarenkorb
                                    ? bestellterDownloadWarenkorb.getAnzahl()
                                    : 0;
                    return anzahlCDs + anzahlDownloads;
                });
        this.verfuegbareDownloadsELCache = new ELValueCache<>(null,
                () -> downloadsLieferungService.lieferungen(hoererSession.getHoerernummer()));
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
        if (hoererSession.isHoererIstUnbekannt()) {
            this.hoerernummerFormular = hoerernummerAusFormular;
            LOGGER.debug("Im Formular eingegebene Hörernummer: {}", hoerernummerAusFormular);
        }
    }

    public boolean hasHoerername() {
        return hoererSession.isHoererIstBekannt()
                && hoererSession.hasHoerername();
    }

    public String getHoerername() {
        return hoererSession.isHoererIstBekannt()
                ? hoererSession.getHoerername().toString()
                : hoerernameAusFormular;
    }

    public void setHoerername(final String hoerernameAusFormular) {
        if (hoererSession.isHoererIstUnbekannt()) {
            this.hoerernameAusFormular = hoerernameAusFormular;
        }
    }

    public boolean hasHoereremail() {
        /* TODO Kann immer geändert werden!? */
        return false; //hoererSession.hasHoereremail();
    }

    public String getHoereremail() {
        return hoererSession.isHoererIstBekannt()
                ? hoererSession.getHoereremail().toString()
                : hoereremailAusFormular;
    }

    public void setHoereremail(final String hoereremailAusFormular) {
        this.hoereremailAusFormular = hoereremailAusFormular;
    }

    //
    // Bestellung
    //

    public int getAnzahlImWarenkorb() {
        LOGGER.trace("");
        return meinWarenkorb.getAnzahl();
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
        bestellungStatusNachricht = "Ihre Bestellung wird bearbeitet!";
        warenkoerbeKopieren();
        final Optional<BestellungId> bestellungId = bestellungAufgeben();
        if (bestellungId.isPresent()) {
            meinWarenkorb.bestellungAufgegeben();
            meineDownloads.bestellungAufgegeben();
            LOGGER.info("Bestellung {} für Hörer {} wurde erfolgreich aufgegeben",
                    bestellungId.get(), getHoerernummer());
            return navigation.zuBestellungErfolgreich();
        } else {
            bestellterCdWarenkorb = null;
            bestellterDownloadWarenkorb = null;
            bestellungStatusNachricht = "Ihre Bestellung konnte leider nicht bearbeitet werden," +
                    " bitte wenden Sie sich an die WBH.";
            LOGGER.error("Eine Bestellung für Hörer {} konnte nicht aufgegeben werden!",
                    getHoerernummer());
            return null; //navigation.zuMeinemWarenkorb();
        }
    }

    private Optional<BestellungId> bestellungAufgeben() {
        return bestellungService.bestellungAufgeben(hoererSession.getBestellungSessionId(),
                new Hoerernummer(getHoerernummer()),
                Hoerername.of(getHoerername()), new HoererEmail(getHoereremail()),
                bemerkung, bestellkarteMischen, alteBestellkarteLoeschen);
    }

    private void warenkoerbeKopieren() {
        bestellterCdWarenkorb = bestellungService
                .cdWarenkorbKopie(hoererSession.getBestellungSessionId());
        bestellterDownloadWarenkorb = bestellungService
                .downloadWarenkorbKopie(hoererSession.getBestellungSessionId());
    }

    public String getBestellungStatusNachricht() {
        return bestellungStatusNachricht;
    }

    public int getBestellteAnzahl() {
        LOGGER.trace("");
        return anzahlBestellterHoerbuecherValueCache.get();
    }

    public CdWarenkorb getBestellterCdWarenkorb() {
        return bestellterCdWarenkorb;
    }

    public DownloadWarenkorb getBestellterDownloadWarenkorb() {
        return bestellterDownloadWarenkorb;
    }

    public boolean isBestellungenVorhanden() {
        if (hoererSession.isHoererIstUnbekannt()) {
            return false;
        } else {
            return bestellungService.anzahlBestellungen(hoererSession.getHoerernummer()) > 0
                    || !verfuegbareDownloadsELCache.get().alle().isEmpty();
        }
    }

}
