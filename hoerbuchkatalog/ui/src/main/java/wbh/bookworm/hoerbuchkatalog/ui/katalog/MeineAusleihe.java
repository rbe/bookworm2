/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.CdLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Component
@RequestScope
public class MeineAusleihe {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineAusleihe.class);

    private final HoererSession hoererSession;

    private final Hoerernummer hoerernummer;

    private final CdLieferungService cdLieferungService;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final Stichwortsuche<Belastung> stichwortsuche;

    @Autowired
    public MeineAusleihe(final HoererSession hoererSession,
                         final CdLieferungService cdLieferungService,
                         final HoerbuchkatalogService hoerbuchkatalogService) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        this.hoerernummer = hoererSession.getHoerernummer();
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.stichwortsuche = new Stichwortsuche<>(cdLieferungService.belastungen(hoerernummer));
    }

    public boolean belastungenVorhanden() {
        return !cdLieferungService.belastungen(hoerernummer).isEmpty();
    }

    public String autor(final Titelnummer titelnummer) {
        return hoererSession.autorCache(titelnummer);
    }

    public String spieldauer(final Titelnummer titelnummer) {
        return hoererSession.spieldauerCache(titelnummer);
    }

    public String titel(final Titelnummer titelnummer) {
        return hoererSession.titelCache(titelnummer);
    }

    public String getStichwort() {
        return stichwortsuche.getStichwort();
    }

    public void setStichwort(final String stichwort) {
        stichwortsuche.setStichwort(stichwort);
    }

    public void sucheNachStichwort() {
        LOGGER.debug("Suche nach Stichwort '{}'", stichwortsuche.getStichwort());
        stichwortsuche.sucheNachStichwort((belastung, s) -> {
            final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer,
                    belastung.getTitelnummer());
            return belastung.getTitelnummer().getValue().equals(s)
                    || hoerbuch.getTitel().contains(s)
                    || hoerbuch.getAutor().contains(s);
        });
    }

    public boolean belastungenAnzeigen() {
        return stichwortsuche.isStichwortEingegeben()
                ? stichwortsuche.isStichwortHatTreffer()
                : !cdLieferungService.belastungen(hoerernummer).isEmpty();
    }

    public List<Belastung> getGefilterteBelastungen() {
        LOGGER.trace("stichwortsuche.isStichwortHatTreffer()={}, {} Ergebnisse",
                stichwortsuche.isStichwortHatTreffer(), stichwortsuche.getGefiltert().size());
        return stichwortsuche.isStichwortHatTreffer()
                ? stichwortsuche.getGefiltert()
                : cdLieferungService.belastungen(hoerernummer);
    }

    public void stichwortVergessen() {
        stichwortsuche.stichwortVergessen();
    }

}
