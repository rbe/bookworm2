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
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequestScope
public class MeineBestellkarte {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineBestellkarte.class);

    private final HoererSession hoererSession;

    private final Hoerernummer hoerernummer;

    private final CdLieferungService cdLieferungService;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final Stichwortsuche<Bestellkarte> stichwortsuche;

    @Autowired
    public MeineBestellkarte(final HoererSession hoererSession,
                             final CdLieferungService cdLieferungService,
                             final HoerbuchkatalogService hoerbuchkatalogService) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        this.hoerernummer = hoererSession.getHoerernummer();
        this.cdLieferungService = cdLieferungService;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.stichwortsuche = new Stichwortsuche<>(cdLieferungService.bestellkarten(hoerernummer));
    }

    public String getLetztesBestelldatumAufDeutsch() {
        final List<Bestellkarte> bestellkarten = cdLieferungService.bestellkarten(hoerernummer);
        return !bestellkarten.isEmpty()
                ? bestellkarten.get(0).getLetztesBestelldatumAufDeutsch()
                : LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public boolean bestellkartenVorhanden() {
        return !cdLieferungService.bestellkarten(hoerernummer).isEmpty();
    }

    public String sachgebiet(final Titelnummer titelnummer) {
        return hoererSession.sachgebietCache(titelnummer).getLabel();
    }

    public String autor(final Titelnummer titelnummer) {
        return hoererSession.autorCache(titelnummer);
    }

    public String titel(final Titelnummer titelnummer) {
        return hoererSession.titelCache(titelnummer);
    }

    public String spieldauer(final Titelnummer titelnummer) {
        return hoererSession.spieldauerCache(titelnummer);
    }

    public String sprecher(final Titelnummer titelnummer) {
        return hoererSession.sprecherCache(titelnummer);
    }

    public String getStichwort() {
        return stichwortsuche.getStichwort();
    }

    public void setStichwort(final String stichwort) {
        stichwortsuche.setStichwort(stichwort);
    }

    public void sucheNachStichwort() {
        LOGGER.debug("Suche nach Stichwort '{}'", stichwortsuche.getStichwort());
        stichwortsuche.sucheNachStichwort((bestellkarte, s) -> {
            LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, bestellkarte.getTitelnummer());
            final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer,
                    bestellkarte.getTitelnummer());
            return bestellkarte.getTitelnummer().getValue().equals(s)
                    || hoerbuch.getTitel().contains(s)
                    || hoerbuch.getAutor().contains(s);
        });
    }

    public boolean bestellkartenAnzeigen() {
        return stichwortsuche.isStichwortEingegeben()
                ? stichwortsuche.isStichwortHatTreffer()
                : !cdLieferungService.erledigteBestellkarten(hoerernummer).isEmpty();
    }

    public List<Bestellkarte> getGefilterteBestellkarten() {
        LOGGER.trace("stichwortsuche.isStichwortHatTreffer()={}, {} Ergebnisse",
                stichwortsuche.isStichwortHatTreffer(), stichwortsuche.getGefiltert().size());
        return stichwortsuche.isStichwortHatTreffer()
                ? stichwortsuche.getGefiltert()
                : cdLieferungService.bestellkarten(hoerernummer);
    }

    public void sucheVergessen() {
        stichwortsuche.stichwortVergessen();
    }

}
