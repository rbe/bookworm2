/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDownload;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;

import aoc.jsf.ELValueCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SessionScope
public class MeineDownloads {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineDownloads.class);

    private final MeineBestellung meineBestellung;

    private final ELValueCache<HoererBlistaDownloads> verfuegbareDownloadsELCache;

    private String stichwort;

    private List<BlistaDownload> nachStichwortGefilterteDownloads;

    @Autowired
    public MeineDownloads(final HoererSession hoererSession,
                          final DownloadsLieferungService downloadsLieferungService,
                          final MeineBestellung meineBestellung) {
        final Hoerernummer hoerernummer = hoererSession.hoerernummer();
        LOGGER.trace("Initialisiere für Hörer {}", hoerernummer);
        this.meineBestellung = meineBestellung;
        this.verfuegbareDownloadsELCache = new ELValueCache<>(null,
                () -> downloadsLieferungService.lieferungen(hoerernummer));
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
        nachStichwortGefilterteDownloads = verfuegbareDownloadsELCache.get().alle()
                .stream()
                .filter(h -> h.getAutor().toLowerCase().contains(stichwort.toLowerCase())
                        || h.getTitel().toLowerCase().contains(stichwort.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean isStichwortHatTreffer() {
        return !nachStichwortGefilterteDownloads.isEmpty();
    }

    public LocalDateTime standVom() {
        return verfuegbareDownloadsELCache.get().getStandVom();
    }

    public List<BlistaDownload> getDownloads() {
        return nachStichwortGefilterteDownloads.isEmpty()
                ? verfuegbareDownloadsELCache.get().alle()
                : nachStichwortGefilterteDownloads;
    }

    public boolean isHoerbuecherAnzeigen() {
        return meineBestellung.isBestellungenVorhanden() || !nachStichwortGefilterteDownloads.isEmpty();
    }

}
