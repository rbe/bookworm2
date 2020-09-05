/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Component
@RequestScope
public class MeinArchiv {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeinArchiv.class);

    private final HoererSession hoererSession;

    private final Hoerernummer hoerernummer;

    //private final CdLieferungService cdLieferungService;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final Stichwortsuche<ErledigteBestellkarte> stichwortsuche;

    private LocalDate suchdatumAb;

    @Autowired
    public MeinArchiv(final HoererSession hoererSession,
                      //final CdLieferungService cdLieferungService,
                      final HoerbuchkatalogService hoerbuchkatalogService) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        this.hoerernummer = hoererSession.getHoerernummer();
        //this.cdLieferungService = cdLieferungService;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        //this.stichwortsuche = new Stichwortsuche<>(cdLieferungService.erledigteBestellkarten(hoerernummer));
        this.stichwortsuche = new Stichwortsuche<>(hoererSession.alleErledigtenBestellkarten());
    }

    public boolean erledigteBestellkartenVorhanden() {
        //return !cdLieferungService.erledigteBestellkarten(hoerernummer).isEmpty();
        return !hoererSession.alleErledigtenBestellkarten().isEmpty();
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

    public String getSuchdatumAb() {
        return null != suchdatumAb
                ? suchdatumAb.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    public void setSuchdatumAb(final String suchdatumAb) {
        if (null != suchdatumAb && !suchdatumAb.isBlank()) {
            this.suchdatumAb =
                    Arrays.stream(new String[]{"dd.MM.yyyy", "dd.M.yyyy", "d.M.yyyy", "d.MM.yyyy"})
                            .map(maybeLocalDate(suchdatumAb))
                            .filter(Objects::nonNull)
                            .findFirst().orElse(null);
        } else {
            this.suchdatumAb = null;
        }
    }

    private Function<String, LocalDate> maybeLocalDate(final String suchdatumAb) {
        return p -> {
            try {
                return LocalDate.parse(suchdatumAb, DateTimeFormatter.ofPattern(p));
            } catch (DateTimeParseException e) {
                // ignore
                return null;
            }
        };
    }

    public void sucheNachStichwort() {
        LOGGER.debug("Suche nach Stichwort '{}'", stichwortsuche.getStichwort());
        stichwortsuche.sucheNachStichwort((erledigteBestellkarte, s) -> {
            LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, erledigteBestellkarte.getTitelnummer());
            final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(hoerernummer,
                    erledigteBestellkarte.getTitelnummer());
            if (null != suchdatumAb) {
                return (null != erledigteBestellkarte.getAusleihdatum()
                        && erledigteBestellkarte.getAusleihdatum().isAfter(suchdatumAb))
                        && (erledigteBestellkarte.getTitelnummer().getValue().equalsIgnoreCase(s)
                        || hoerbuch.getTitel().toLowerCase().contains(s.toLowerCase())
                        || hoerbuch.getAutor().toLowerCase().contains(s.toLowerCase()));
            } else {
                return erledigteBestellkarte.getTitelnummer().getValue().equalsIgnoreCase(s)
                        || hoerbuch.getTitel().toLowerCase().contains(s.toLowerCase())
                        || hoerbuch.getAutor().toLowerCase().contains(s.toLowerCase());
            }
        });
    }

    public boolean erledigteBestellkartenAnzeigen() {
        return stichwortsuche.isStichwortEingegeben()
                ? stichwortsuche.isStichwortHatTreffer()
                //: !cdLieferungService.erledigteBestellkarten(hoerernummer).isEmpty();
                : !hoererSession.alleErledigtenBestellkarten().isEmpty();
    }

    public List<ErledigteBestellkarte> getGefilterteErledigteBestellkarten() {
        LOGGER.trace("stichwortsuche.isStichwortHatTreffer()={}, {} Ergebnisse",
                stichwortsuche.isStichwortHatTreffer(), stichwortsuche.getGefiltert().size());
        return stichwortsuche.isStichwortHatTreffer()
                ? stichwortsuche.getGefiltert()
                //: /* TODO Sortieren */cdLieferungService.erledigteBestellkarten(hoerernummer);
                : hoererSession.alleErledigtenBestellkarten();
    }

    public void sucheVergessen() {
        stichwortsuche.stichwortVergessen();
        suchdatumAb = null;
    }

}
