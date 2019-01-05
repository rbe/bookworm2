/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.HoerbuchkatalogAktualisiert;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDownload;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;

import aoc.ddd.event.DomainEventPublisher;
import aoc.ddd.event.DomainEventSubscriber;
import aoc.jsf.ELFunctionCache;
import aoc.jsf.ELValueCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@SessionScope
public class HoererSession implements Serializable {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(HoererSession.class);

    private final transient HttpSession session;

    @Autowired
    public HoererSession(final HttpServletRequest request,
                         final HttpSession session,
                         final HoerbuchkatalogService hoerbuchkatalogService,
                         final BestellungService bestellungService,
                         final MerklisteService merklisteService,
                         final DownloadsLieferungService downloadsLieferungService) {
        LOGGER.trace("Initialisiere für HttpSession {}", session.getId());
        this.session = session;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.merklisteService = merklisteService;
        this.bestellungService = bestellungService;
        this.downloadsLieferungService = downloadsLieferungService;
        final String hnr = request.getParameter(SessionKey.HOERERNUMMER);
        final Hoerernummer hoerernummer = null != hnr && !hnr.isBlank()
                ? new Hoerernummer(hnr)
                : Hoerernummer.UNBEKANNT;
        hoererSetzen(hoerernummer);
        this.bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        this.suchparameter = new Suchparameter();
        DomainEventPublisher.global()
                .subscribe(new BestellungAufgegebenDomainEventSubscriber());
        DomainEventPublisher.global()
                .subscribe(new HoerbuchkatalogAktualisiertDomainEventSubscriber());
    }

    //
    // Hörer
    //

    private Hoerer hoerer;

    void hoererSetzen(final Hoerernummer hoerernummer) {
        Objects.requireNonNull(hoerernummer);
        // Nur (einmal) erlauben, wenn kein oder unbekannter Hörer
        if (null == hoerer || hoerer.isUnbekannt()) {
            LOGGER.trace("Setze Hörer {} in HttpSession {}", hoerernummer, session.getId());
            session.setAttribute(SessionKey.HOERERNUMMER, hoerernummer);
            hoerer = /* TODO Daten aus Nutzerbereich abfragen oder per Request übergeben? */
                    new Hoerer(hoerernummer, Hoerername.UNBEKANNT, HoererEmail.UNBEKANNT);
            // Hörbuchkatalog
            hoerbuchValueCache = new ELFunctionCache<>(titelnummer ->
                    hoerbuchkatalogService.hole(hoerernummer, titelnummer));
            // Merkliste
            merklisteValueCache = new ELValueCache<>(null, () ->
                    merklisteService.merklisteKopie(hoerernummer));
            // CD Warenkorb
            cdWarenkorbValueCache = new ELValueCache<>(null, () ->
                    bestellungService.cdWarenkorbKopie(bestellungSessionId, hoerernummer));
            // Download Warenkorb
            downloadWarenkorbValueCache = new ELValueCache<>(null, () ->
                    bestellungService.downloadWarenkorbKopie(bestellungSessionId, hoerernummer));
            maxDownloadsProTagErreicht = new ELValueCache<>(Boolean.FALSE, () ->
                    bestellungService.isMaxDownloadsProTagErreicht(bestellungSessionId, hoerernummer));
            maxDownloadsProMonatErreicht = new ELValueCache<>(Boolean.FALSE, () ->
                    bestellungService.isMaxDownloadsProMonatErreicht(bestellungSessionId, hoerernummer));
            // Lieferung
            blistaDownloadsELCache = new ELValueCache<>(null, () ->
                    downloadsLieferungService.lieferungen(hoerernummer));
            LOGGER.debug("Hörer {} hat HttpSession {}", hoerernummer, session.getId());
        } else {
            LOGGER.warn("Hörer {} bereits in HttpSession {} gesetzt", hoerernummer, session.getId());
        }
    }

    public Hoerernummer getHoerernummer() {
        return hoerer.getHoerernummer();
    }

    public boolean isHoererIstBekannt() {
        return hoerer.isBekannt();
    }

    public boolean isHoererIstUnbekannt() {
        return hoerer.isUnbekannt();
    }

    boolean hasHoerername() {
        return hoerer.hasHoerername();
    }

    public Hoerername getHoerername() {
        return hoerer.getHoerername();
    }

    boolean hasHoereremail() {
        return hoerer.hasHoereremail();
    }

    HoererEmail getHoereremail() {
        return hoerer.getHoereremail();
    }

    //
    // Hörbuchkatalog
    //

    private final transient HoerbuchkatalogService hoerbuchkatalogService;

    private transient ELFunctionCache<Titelnummer, Hoerbuch> hoerbuchValueCache;

    private Suchparameter suchparameter;

    private Hoerbuch gemerktesHoerbuch;

    void hoerbuchMerken(final Titelnummer titelnummer) {
        gemerktesHoerbuch = hoerbuchkatalogService.hole(getHoerernummer(), titelnummer);
    }

    Hoerbuch gemerktesHoerbuch() {
        return gemerktesHoerbuch;
    }

    void gemerktesHoerbuchVergessen() {
        gemerktesHoerbuch = null;
    }

    ELFunctionCache<Titelnummer, Hoerbuch> hoerbuchValueCache() {
        return hoerbuchValueCache;
    }

    boolean hoerbuchIstDownloadbar(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).isDownloadbar();
    }

    String autor(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).getAutor();
    }

    String titel(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).getTitel();
    }

    Suchparameter getSuchparameter() {
        return suchparameter;
    }

    void suchparameterHinzufuegen(final Suchparameter.Feld feld, final String wert) {
        suchparameter.hinzufuegen(feld, wert);
    }

    String wertDesSuchparameters(final Suchparameter.Feld feld) {
        return suchparameter.wert(feld);
    }

    void suchparameterVergessen() {
        suchparameter.leeren();
    }

    //
    // Merkliste
    //

    private final transient MerklisteService merklisteService;

    private transient ELValueCache<Merkliste> merklisteValueCache;

    /* TODO Event? */
    void merklisteVergessen() {
        merklisteValueCache.invalidate();
    }

    int anzahlTitelnummernAufMerkliste() {
        return merklisteValueCache.get().getAnzahl();
    }

    Set<Titelnummer> titelnummernAufMerkliste() {
        return merklisteValueCache.get().getTitelnummern();
    }

    boolean titelnummernAufMerklisteEnthalten(final Titelnummer titelnummer) {
        return merklisteValueCache.get().enthalten(titelnummer);
    }

    //
    // CD Warenkorb
    //

    private transient ELValueCache<CdWarenkorb> cdWarenkorbValueCache;

    CdWarenkorb cdWarenkorb() {
        return cdWarenkorbValueCache.get();
    }

    /* TODO BestellungAufgegebenEvent, CdInDenWarenkorbGelegtEvent, DownloadInDenWarenkorbGelegtEvent */
    void cdWarenkorbVergessen() {
        cdWarenkorbValueCache.invalidate();
    }

    boolean cdEnthalten(final Titelnummer titelnummer) {
        return cdWarenkorbValueCache.get().enthalten(titelnummer);
    }

    //
    // Download Warenkorb
    //

    private transient ELValueCache<DownloadWarenkorb> downloadWarenkorbValueCache;

    private transient ELValueCache<Boolean> maxDownloadsProTagErreicht;

    private transient ELValueCache<Boolean> maxDownloadsProMonatErreicht;

    DownloadWarenkorb downloadWarenkorb() {
        return downloadWarenkorbValueCache.get();
    }

    /* TODO BestellungAufgegebenEvent, CdInDenWarenkorbGelegtEvent, DownloadInDenWarenkorbGelegtEvent */
    void downloadWarenkorbVergessen() {
        downloadWarenkorbValueCache.invalidate();
        maxDownloadsProTagErreicht.invalidate();
        maxDownloadsProMonatErreicht.invalidate();
    }

    boolean downloadEnthalten(final Titelnummer titelnummer) {
        return downloadWarenkorbValueCache.get().enthalten(titelnummer);
    }

    boolean maxDownloadsProTagErreicht() {
        return isHoererIstBekannt() && maxDownloadsProTagErreicht.get();
    }

    boolean maxDownloadsProMonatErreicht() {
        return isHoererIstBekannt() && maxDownloadsProMonatErreicht.get();
    }

    //
    // Bestellung
    //

    private final transient BestellungService bestellungService;

    private BestellungSessionId bestellungSessionId;

    private CdWarenkorb bestellterCdWarenkorb;

    private DownloadWarenkorb bestellterDownloadWarenkorb;

    //private transient ELValueCache<Integer> anzahlBestellterHoerbuecherValueCache;

    BestellungSessionId getBestellungSessionId() {
        return bestellungSessionId;
    }

    int anzahlImWarenkorbGesamt() {
        return cdWarenkorbValueCache.get().getAnzahl() +
                downloadWarenkorbValueCache.get().getAnzahl();
    }

    int anzahlBestellterHoerbuecher() {
        //return anzahlBestellterHoerbuecherValueCache.get();
        final int anzahlCDs =
                null != bestellterCdWarenkorb
                        ? bestellterCdWarenkorb.getAnzahl()
                        : 0;
        final int anzahlDownloads =
                null != bestellterDownloadWarenkorb
                        ? bestellterDownloadWarenkorb.getAnzahl()
                        : 0;
        return anzahlCDs + anzahlDownloads;
    }

    CdWarenkorb getBestellterCdWarenkorb() {
        return bestellterCdWarenkorb;
    }

    DownloadWarenkorb getBestellterDownloadWarenkorb() {
        return bestellterDownloadWarenkorb;
    }

    /* TODO Event? */
    void bestellteWarenkoerbeMerken() {
        bestellterCdWarenkorb = bestellungService.cdWarenkorbKopie(
                bestellungSessionId, hoerer.getHoerernummer());
        bestellterDownloadWarenkorb = bestellungService.downloadWarenkorbKopie(
                bestellungSessionId, hoerer.getHoerernummer());
    }

    /* TODO Event? */
    void bestellteWarenkoerbeVergessen() {
        bestellterCdWarenkorb = null;
        bestellterDownloadWarenkorb = null;
    }

    //
    // Lieferung
    //

    private final transient DownloadsLieferungService downloadsLieferungService;

    private transient ELValueCache<HoererBlistaDownloads> blistaDownloadsELCache;

    LocalDateTime standVomDerDownloads() {
        return blistaDownloadsELCache.get().getStandVom();
    }

    boolean isBlistaAbrufHatFehler() {
        if (blistaDownloadsELCache.get().hatFehler()) {
            blistaDownloadsELCache.invalidate();
        }
        return blistaDownloadsELCache.get().hatFehler();
    }

    String blistaFehlercode() {
        return blistaDownloadsELCache.get().getFehlercode();
    }

    String blistaFehlermeldung() {
        return blistaDownloadsELCache.get().getFehlermeldung();
    }

    List<BlistaDownload> alleDownloads() {
        if (blistaDownloadsELCache.get().hatFehler()) {
            blistaDownloadsELCache.invalidate();
        }
        return blistaDownloadsELCache.get().alle();
    }

    List<BlistaDownload> bezugsfaehigeDownloads() {
        if (blistaDownloadsELCache.get().hatFehler()) {
            blistaDownloadsELCache.invalidate();
        }
        return blistaDownloadsELCache.get().bezuegsfaehige();
    }

    /* TODO BestellungAufgegebenEvent
    void downloadsVergessen() {
        blistaDownloadsELCache.invalidate();
        LOGGER.debug("Zwischengespeicherte Downloads geleert; erneute Abfrage bei blista notwendig");
    }
    */

    @Override
    public String toString() {
        return String.format("HoererSession{hoerer=%s, bestellungSessionId=%s, session.id=%s}",
                hoerer, bestellungSessionId, session.getId());
    }

    private class BestellungAufgegebenDomainEventSubscriber
            extends DomainEventSubscriber<BestellungAufgegeben> {

        BestellungAufgegebenDomainEventSubscriber() {
            super(BestellungAufgegeben.class);
        }

        @Override
        public void handleEvent(final BestellungAufgegeben domainEvent) {
            LOGGER.trace("Verarbeite {}", domainEvent);
            // cdWarenkorbVergessen
            cdWarenkorbValueCache.invalidate();
            LOGGER.debug("Cache für CD-Warenkorb invalidiert");
            // downloadWarenkorbVergessen
            downloadWarenkorbValueCache.invalidate();
            maxDownloadsProTagErreicht.invalidate();
            maxDownloadsProMonatErreicht.invalidate();
            LOGGER.debug("Cache für Download-Warenkorb invalidiert");
            // downloadsVergessen
            blistaDownloadsELCache.invalidate();
            LOGGER.debug("Cache für Downloads invalidiert; erneute Abfrage bei blista notwendig");
        }

    }

    private class HoerbuchkatalogAktualisiertDomainEventSubscriber
            extends DomainEventSubscriber<HoerbuchkatalogAktualisiert> {

        // TODO Klasse für das Event nur bei #subscribe angeben?
        HoerbuchkatalogAktualisiertDomainEventSubscriber() {
            super(HoerbuchkatalogAktualisiert.class);
        }

        @Override
        public void handleEvent(final HoerbuchkatalogAktualisiert domainEvent) {
            LOGGER.trace("Verarbeite {}", domainEvent);
        }

    }

}
