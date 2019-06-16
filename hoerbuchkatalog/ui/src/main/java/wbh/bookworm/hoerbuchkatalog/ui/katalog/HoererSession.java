/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererdatenAktualisiert;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.HoerbuchkatalogAktualisiert;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDownload;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;

import aoc.ddd.event.DomainEventPublisher;
import aoc.ddd.event.DomainEventSubscriber;
import aoc.jsf.ELFunctionCache;
import aoc.jsf.ELValueCache;
import aoc.jsf.TimeoutCacheDecorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@SessionScope
public class HoererSession implements Serializable, HttpSessionBindingListener {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(HoererSession.class);

    private final transient HttpSession session;

    @SuppressWarnings({"squid:S00107"})
    @Autowired
    public HoererSession(final HttpServletRequest request,
                         final HttpSession session,
                         final HoererService hoererService,
                         final HoerbuchkatalogService hoerbuchkatalogService,
                         final BestellungService bestellungService,
                         final WarenkorbService warenkorbService,
                         final MerklisteService merklisteService,
                         final DownloadsLieferungService downloadsLieferungService) {
        LOGGER.trace("Initialisiere für HttpSession {}", session.getId());
        this.session = session;
        this.hoererService = hoererService;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.merklisteService = merklisteService;
        this.warenkorbService = warenkorbService;
        this.downloadsLieferungService = downloadsLieferungService;
        final String hnr = request.getParameter(SessionKey.HOERERNUMMER);
        final Hoerernummer hoerernummer = null != hnr && !hnr.isBlank()
                ? new Hoerernummer(hnr)
                : Hoerernummer.UNBEKANNT;
        hoererSetzen(hoerernummer);
        this.bestellungService = bestellungService;
        this.bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
        this.suchparameter = new Suchparameter();
        DomainEventPublisher.global()
                .subscribe(new HoerbuchkatalogAktualisiertSubscriber());
    }

    //
    // Hörbuchkatalog
    //

    private class HoerbuchkatalogAktualisiertSubscriber
            extends DomainEventSubscriber<HoerbuchkatalogAktualisiert> {

        // TODO Klasse für das Event nur bei #subscribe angeben?
        HoerbuchkatalogAktualisiertSubscriber() {
            super(HoerbuchkatalogAktualisiert.class);
        }

        @Override
        public void handleEvent(final HoerbuchkatalogAktualisiert domainEvent) {
            logger.trace("Verarbeite {}", domainEvent);
            hoerbuchValueCache.invalidateAll();
        }

    }

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

    Sachgebiet sachgebietCache(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).getSachgebiet();
    }

    String autorCache(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).getAutor();
    }

    String titelCache(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).getTitel();
    }

    String spieldauerCache(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).getSpieldauer();
    }

    String sprecherCache(final Titelnummer titelnummer) {
        return hoerbuchValueCache.get(titelnummer).getSprecher();
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

    private class BestellungAufgegebenSubscriber
            extends DomainEventSubscriber<BestellungAufgegeben> {

        BestellungAufgegebenSubscriber() {
            super(BestellungAufgegeben.class);
        }

        @Override
        public void handleEvent(final BestellungAufgegeben domainEvent) {
            if (domainEvent.getHoerernummer().equals(hoerernummer)) {
                logger.trace("Verarbeite {}", domainEvent);
                // cdWarenkorbVergessen
                cdWarenkorbValueCache.invalidate();
                logger.debug("Cache für CD-Warenkorb invalidiert");
                // downloadWarenkorbVergessen
                downloadWarenkorbValueCache.invalidate();
                maxDownloadsProTagErreicht.invalidate();
                maxDownloadsProMonatErreicht.invalidate();
                logger.debug("Cache für Download-Warenkorb invalidiert");
                // downloadsVergessen
                blistaDownloadsELCache.invalidate();
                logger.debug("Cache für Downloads invalidiert; erneute Abfrage bei blista notwendig");
                //
                bestellungSessionId = bestellungService.bestellungSessionId(hoerernummer);
            }
        }

    }

    private final transient WarenkorbService warenkorbService;

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

    /* TODO Event? Oder für die Bestätigungsseite an den Request hängen */
    void bestellteWarenkoerbeMerken() {
        bestellterCdWarenkorb = warenkorbService.cdWarenkorbKopie(
                bestellungSessionId, hoerernummer);
        bestellterDownloadWarenkorb = warenkorbService.downloadWarenkorbKopie(
                bestellungSessionId, hoerernummer);
    }

    /* TODO Event? Oder für die Bestätigungsseite an den Request hängen */
    void bestellteWarenkoerbeVergessen() {
        bestellterCdWarenkorb = null;
        bestellterDownloadWarenkorb = null;
    }

    //
    // Lieferung
    //

    private final transient DownloadsLieferungService downloadsLieferungService;

    private transient TimeoutCacheDecorator<HoererBlistaDownloads> blistaDownloadsELCache;

    LocalDateTime standVomDerDownloads() {
        return blistaDownloadsELCache.get().getStandVom();
    }

    boolean isBlistaAbrufHatFehler() {
        final boolean zugriffVerweigert = false;//blistaDownloadsELCache.get().getFehlercode().equals("1");
        final boolean keineDownloadsIn365Tagen = blistaDownloadsELCache.get().getFehlercode().equals("202");
        if (!zugriffVerweigert && !keineDownloadsIn365Tagen
                && blistaDownloadsELCache.get().hatFehler()) {
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

    //
    // Hörer
    //

    private class HoererdatenAktualisiertSubscriber
            extends DomainEventSubscriber<HoererdatenAktualisiert> {

        HoererdatenAktualisiertSubscriber() {
            super(HoererdatenAktualisiert.class);
        }

        @Override
        public void handleEvent(final HoererdatenAktualisiert domainEvent) {
            if (domainEvent.getHoerernummer().equals(hoerernummer)) {
                logger.trace("Verarbeite {}", domainEvent);
                hoererELCache.invalidate();
            } else {
                logger.warn("Hörer {} hat {} für Hörer {} erhalten",
                        hoerernummer, domainEvent, domainEvent.getHoerernummer());
            }
        }

    }

    private final transient HoererService hoererService;

    private Hoerernummer hoerernummer;

    private transient ELValueCache<Hoerer> hoererELCache;

    void hoererSetzen(final Hoerernummer hoerernummer) {
        Objects.requireNonNull(hoerernummer);
        // Nur (einmal) erlauben, wenn kein oder unbekannter Hörer
        if (null == hoererELCache || (hoerernummer.isBekannt() && hoererELCache.get().isUnbekannt())) {
            // Hörer
            LOGGER.trace("Setze Hörer {} in HttpSession {}", hoerernummer, session.getId());
            this.hoerernummer = hoerernummer;
            session.setAttribute(SessionKey.HOERERNUMMER, hoerernummer);
            hoererELCache = new ELValueCache<>(Hoerer.UNBEKANNT, () ->
                    hoererService.hoerer(hoerernummer));
            DomainEventPublisher.global()
                    .subscribe(new HoererdatenAktualisiertSubscriber());
            // Hörbuchkatalog
            hoerbuchValueCache = new ELFunctionCache<>(titelnummer ->
                    hoerbuchkatalogService.hole(hoerernummer, titelnummer));
            // Merkliste
            merklisteValueCache = new ELValueCache<>(null, () ->
                    merklisteService.merklisteKopie(hoerernummer));
            // CD Warenkorb
            cdWarenkorbValueCache = new ELValueCache<>(null, () ->
                    warenkorbService.cdWarenkorbKopie(bestellungSessionId, hoerernummer));
            // Download Warenkorb
            downloadWarenkorbValueCache = new ELValueCache<>(null, () ->
                    warenkorbService.downloadWarenkorbKopie(bestellungSessionId, hoerernummer));
            maxDownloadsProTagErreicht = new ELValueCache<>(Boolean.FALSE, () ->
                    warenkorbService.isMaxDownloadsProTagErreicht(bestellungSessionId, hoerernummer));
            maxDownloadsProMonatErreicht = new ELValueCache<>(Boolean.FALSE, () ->
                    warenkorbService.isMaxDownloadsProMonatErreicht(bestellungSessionId, hoerernummer));
            // Bestellung
            DomainEventPublisher.global()
                    .subscribe(new BestellungAufgegebenSubscriber());
            // Lieferung
            blistaDownloadsELCache = new TimeoutCacheDecorator<>(new ELValueCache<>(
                    null, () -> downloadsLieferungService.lieferungen(hoerernummer)),
                    TimeUnit.MINUTES.toMillis(5));
            LOGGER.info("Hörer {} erfolgreich angemeldet, HttpSession {}", hoerernummer, session.getId());
        } else {
            LOGGER.warn("Hörer {} bereits in HttpSession {} gesetzt", hoerernummer, session.getId());
        }
    }

    public Hoerernummer getHoerernummer() {
        return hoererELCache.get().getHoerernummer();
    }

    public boolean isHoererIstBekannt() {
        return hoererELCache.get().isBekannt();
    }

    public boolean isHoererIstUnbekannt() {
        return hoererELCache.get().isUnbekannt();
    }

    boolean hasHoerername() {
        return hoererELCache.get().hasHoerername();
    }

    public Hoerername getHoerername() {
        return hoererELCache.get().getHoerername();
    }

    boolean hasHoereremail() {
        return hoererELCache.get().hasHoereremail();
    }

    HoererEmail getHoereremail() {
        return hoererELCache.get().getHoereremail();
    }

    //
    // HttpSessionBindingListener
    //

    @Override
    public void valueUnbound(final HttpSessionBindingEvent event) {
        // TODO bestellungService.sessionBeenden(bestellungSessionId);
        warenkorbService.cdWarenkorbLoeschen(bestellungSessionId, hoerernummer);
        if (hoerernummer.isBekannt()) {
            warenkorbService.downloadWarenkorbLoeschen(bestellungSessionId, hoerernummer);
        }
        LOGGER.debug("HoererSession aus HttpSession {} für Hörer {}, BestellungSessionId {} entfernt",
                event.getSession().getId(), hoerernummer, bestellungSessionId);
    }

    @Override
    public String toString() {
        return String.format("HoererSession{hoerer=%s, bestellungSessionId=%s, session.id=%s}",
                hoererELCache.get(), bestellungSessionId, session.getId());
    }

}
