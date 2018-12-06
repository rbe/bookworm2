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
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineBestellung.class);

    private final Navigation navigation;

    //
    // Hörer
    //

    private Hoerernummer hoerernummer;

    private Hoerer hoerer;

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

    private final transient DownloadsLieferungService downloadsLieferungService;

    private final transient ELValueCache<HoererBlistaDownloads> verfuegbareDownloadsELCache;

    @Autowired
    public MeineBestellung(final Navigation navigation,
                           final HoererSession hoererSession,
                           final MeinWarenkorb meinWarenkorb,
                           final BestellungService bestellungService,
                           final DownloadsLieferungService downloadsLieferungService) {
        this.hoerernummer = hoererSession.getHoerernummer();
        LOGGER.trace("Initialisiere für Hörer {}", hoerernummer);
        this.hoerer = hoererSession.getHoerer();
        this.navigation = navigation;
        this.meinWarenkorb = meinWarenkorb;
        this.bestellungService = bestellungService;
        this.downloadsLieferungService = downloadsLieferungService;
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
                () -> this.downloadsLieferungService.lieferungen(hoerernummer));
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public void setHoerernummer(final Hoerernummer hoerernummer) {
        LOGGER.trace("{}", hoerernummer);
        if (Hoerernummer.UNBEKANNT == hoerernummer) {
            this.hoerernummer = hoerernummer;
        } else {
            /*TODO Bekannte Hörernummer*/
            throw new UnsupportedOperationException();
        }
    }

    public boolean hasHoerername() {
        return hoerer.hasHoerername();
    }

    public String getHoerername() {
        return String.format("%s %s",
                hoerer.hasVorname() ? hoerer.getVorname() : "Unbekannt",
                hoerer.hasNachname() ? hoerer.getNachname() : "Unbekann");
    }

    public boolean hasHoereremail() {
        return hoerer.hasHoereremail();
    }

    public HoererEmail getHoereremail() {
        return hoerer.getHoereremail();
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

    /**
     * Command
     */
    public String bestellungAbschicken() {
        bestellungStatusNachricht = "Ihre Bestellung wird bearbeitet!";
        // Warenkörbe kopieren
        bestellterCdWarenkorb = bestellungService.cdWarenkorbKopie(hoerernummer);
        bestellterDownloadWarenkorb = bestellungService.downloadWarenkorbKopie(hoerernummer);
        // Bestellung aufgeben
        final Optional<BestellungId> bestellungId = bestellungService.bestellungAufgeben(
                hoerernummer,
                hoerer.getHoerername(), hoerer.getHoereremail(),
                bemerkung,
                bestellkarteMischen, alteBestellkarteLoeschen);
        if (bestellungId.isPresent()) {
            meinWarenkorb.bestellungAufgegeben();
            LOGGER.info("Bestellung {} für Hörer {} wurde erfolgreich aufgegeben",
                    bestellungId.get(), hoerernummer);
            return navigation.zuBestellungErfolgreich();
        } else {
            LOGGER.error("Bestellung für Hörer {} konnte nicht aufgegeben werden!", hoerernummer);
            bestellterCdWarenkorb = null;
            bestellterDownloadWarenkorb = null;
            bestellungStatusNachricht = "Ihre Bestellung konnte leider nicht bearbeitet werden," +
                    " bitte wenden Sie sich an die WBH.";
            return null; //navigation.zuMeinemWarenkorb();
        }
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
        return bestellungService.anzahlBestellungen(hoerernummer) > 0
                || !verfuegbareDownloadsELCache.get().alle().isEmpty();
    }

}
