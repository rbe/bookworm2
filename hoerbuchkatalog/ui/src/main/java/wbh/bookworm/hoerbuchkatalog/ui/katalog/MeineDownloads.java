/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDownload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequestScope
public class MeineDownloads {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineDownloads.class);

    private final HoererSession hoererSession;

    private final MeineBestellung meineBestellung;

    private String stichwort;

    private List<BlistaDownload> nachStichwortGefilterteDownloads;

    @Autowired
    public MeineDownloads(final HoererSession hoererSession,
                          final MeineBestellung meineBestellung) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        this.meineBestellung = meineBestellung;
        nachStichwortGefilterteDownloads = Collections.emptyList();
    }

    public String getStichwort() {
        return stichwort;
    }

    public void setStichwort(final String stichwort) {
        this.stichwort = stichwort;
        if (null != stichwort && stichwort.isBlank()) {
            nachStichwortGefilterteDownloads = Collections.emptyList();
        }
    }

    public void sucheNachStichwort() {
        nachStichwortGefilterteDownloads = hoererSession.alleDownloads()
                .stream()
                .filter(h -> h.getAutor().toLowerCase().contains(stichwort.toLowerCase())
                        || h.getTitel().toLowerCase().contains(stichwort.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean isStichwortHatKeineTreffer() {
        return null == stichwort || stichwort.isBlank()
                || !nachStichwortGefilterteDownloads.isEmpty();
    }

    public List<BlistaDownload> getAlleDownloads() {
        return nachStichwortGefilterteDownloads.isEmpty()
                ? hoererSession.alleDownloads()
                : nachStichwortGefilterteDownloads;
    }

    public boolean isHoerbuecherAnzeigen() {
        return (!hoererSession.blistaAbrufHatFehler()
                && meineBestellung.isBestellungenVorhanden())
                || !nachStichwortGefilterteDownloads.isEmpty();
    }

    public boolean isBlistaAbrufHatFehler() {
        return hoererSession.blistaAbrufHatFehler();
    }

    public String getBlistaFehlercode() {
        return hoererSession.blistaFehlercode();
    }

    public String getBlistaFehlermeldung() {
        return hoererSession.blistaFehlermeldung();
    }

    /* TODO BestellungAufgegebenEvent
    void bestellungAufgegeben() {
        hoererSession.downloadsVergessen();
    }
    */

}
