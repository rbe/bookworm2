/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.download.lieferung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;

import aoc.jsf.ELValueCache;

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

    private final Navigation navigation;

    //
    // Hörer
    //

    private Hoerernummer hoerernummer;

    private String hoerername;

    private String hoereremail;

    //
    // Bestellung
    //

    private final MeinWarenkorb meinWarenkorb;

    private final BestellungService bestellungService;

    private String bemerkung;

    private Boolean bestellkarteMischen;

    private Boolean alteBestellkarteLoeschen;

    private String bestellungStatusNachricht;

    private CdWarenkorb bestellterCdWarenkorb;

    private DownloadWarenkorb bestellterDownloadWarenkorb;

    private final ELValueCache<Integer> anzahlBestellterHoerbuecherValueCache;

    private final DownloadsLieferungService downloadsLieferungService;

    private final ELValueCache<HoererBlistaDownloads> verfuegbareDownloadsELCache;

    @Autowired
    public MeineBestellung(final Navigation navigation,
                           final Hoerernummer hoerernummer,
                           final MeinWarenkorb meinWarenkorb,
                           final BestellungService bestellungService,
                           final DownloadsLieferungService downloadsLieferungService) {
        this.downloadsLieferungService = downloadsLieferungService;
        LOGGER.trace("Initialisiere für Hörer {}", hoerernummer);
        this.navigation = navigation;
        this.hoerernummer = hoerernummer;
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
        return null != hoerername && !hoerername.trim().isEmpty();
    }

    public String getHoerername() {
        return hoerername;
    }

    public void setHoerername(final String hoerername) {
        this.hoerername = hoerername;
    }

    public boolean hasHoereremail() {
        return null != hoereremail && !hoereremail.trim().isEmpty();
    }

    public String getHoereremail() {
        return hoereremail;
    }

    public void setHoereremail(final String hoereremail) {
        this.hoereremail = hoereremail;
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
                hoerername, hoereremail,
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
