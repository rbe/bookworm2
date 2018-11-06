/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.HoerbuchkatalogId;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.ddd.model.DomainId;
import aoc.ddd.repository.DomainRespositoryComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

@DomainRespositoryComponent
public final class HoerbuchkatalogRepository
        /* TODO extends JsonDomainRepository<Hoerbuchkatalog, HoerbuchkatalogId>*/ {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogRepository.class);

    private final ApplicationContext applicationContext;

    private final HoerbuchkatalogConfig hoerbuchkatalogConfig;

    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> katalogeAktualisierenScheduledFuture;

    private final HoerbuchkatalogMapper hoerbuchkatalogMapper;

    private final AghNummernRepository aghNummernRepository;

    private final HoerbuchkatalogArchiv hoerbuchkatalogArchiv;

    private AtomicReference<Hoerbuchkatalog> aktuellerHoerbuchkatalog;

    @Autowired
    HoerbuchkatalogRepository(final ApplicationContext applicationContext,
                              final HoerbuchkatalogConfig hoerbuchkatalogConfig,
                              final TaskScheduler taskScheduler,
                              final HoerbuchkatalogMapper hoerbuchkatalogMapper,
                              final AghNummernRepository aghNummernRepository,
                              final HoerbuchkatalogArchiv hoerbuchkatalogArchiv) {
        /* TODO super(Hoerbuchkatalog.class, HoerbuchkatalogId.class, hoerbuchkatalogConfig.getHoerbuchkatalogDirectory());*/
        this.applicationContext = applicationContext;
        this.hoerbuchkatalogConfig = hoerbuchkatalogConfig;
        this.taskScheduler = taskScheduler;
        this.hoerbuchkatalogMapper = hoerbuchkatalogMapper;
        this.aghNummernRepository = aghNummernRepository;
        this.hoerbuchkatalogArchiv = hoerbuchkatalogArchiv;
        this.aktuellerHoerbuchkatalog = new AtomicReference<>();
        archivRegelmaessigAktualisieren();
    }

    @Bean
    Hoerbuchkatalog hoerbuchkatalog() {
        return hoerbuchkatalog(false);
    }

    /**
     * TODO Logik nach GesamtDatRepository#load("Datum") inkl. Selektion LuceneIndex
     */
    private Hoerbuchkatalog hoerbuchkatalog(final boolean neu) {
        if (null == aktuellerHoerbuchkatalog.get() || neu) {
            LOGGER.trace("Erzeuge neuen Hörbuchkatalog");
            final Optional<Path> gesamtDat = hoerbuchkatalogArchiv.findeAktuellstenKatalog("Gesamt.dat");
            gesamtDat.ifPresentOrElse(gd -> {
                        LOGGER.trace("Erzeuge neuen Hörbuchkatalog aus '{}'", gd);
                        final Set<Hoerbuch> hoerbuecher = importiereHoerbuchkatalogAusArchiv(gd);
                        if (hoerbuecher.isEmpty()) {
                            throw new IllegalStateException("Import leer; Keine Hörbücher importiert");
                        } else {
                            final Set<AghNummer> aghNummern = importiereAghNummernAusArchiv();
                            @SuppressWarnings({"unchecked"}) final Map<Titelnummer, Hoerbuch> map = (Map<Titelnummer, Hoerbuch>)
                                    applicationContext.getBean("hoerbuchkatalogMap", Map.class);
                            final HoerbuchkatalogId hoerbuchkatalogDomainId =
                                    new HoerbuchkatalogId(gd.getFileName().toString());
                            final Hoerbuchkatalog neuerKatalog = new Hoerbuchkatalog(hoerbuchkatalogDomainId, map);
                            verheiraten(neuerKatalog, hoerbuecher, aghNummern);
                            sucheInitialisieren(hoerbuchkatalogDomainId, neuerKatalog);
                            aktuellerHoerbuchkatalog.set(neuerKatalog);
                            LOGGER.debug("Hörbuchkatalog {} aus '{}' erzeugt", aktuellerHoerbuchkatalog.get(), gd);
                        }
                    },
                    () -> {
                    });
        }
        LOGGER.debug("Hörbuchkatalog bereits erzeugt, {}", aktuellerHoerbuchkatalog.get());
        return aktuellerHoerbuchkatalog.get();
    }

    private void sucheInitialisieren(final DomainId<String> hoerbuchkatalogDomainId,
                                     final Hoerbuchkatalog hoerbuchkatalog) {
        final HoerbuchkatalogSuche hoerbuchkatalogSuche =
                new HoerbuchkatalogSuche(applicationContext, hoerbuchkatalogDomainId);
        hoerbuchkatalogSuche.indiziere(hoerbuchkatalog.alle());
        hoerbuchkatalog.setHoerbuchkatalogSuche(hoerbuchkatalogSuche);
    }

    private void verheiraten(final Hoerbuchkatalog hoerbuchkatalog,
                             final Set<Hoerbuch> hoerbuecher, final Set<AghNummer> aghNummern) {
        hoerbuecher.forEach(hoerbuch -> {
            istHoerbuchDownloadbar(hoerbuch, aghNummern);
            hoerbuchkatalog.hinzufuegen(hoerbuch);
        });
        LOGGER.info("{} von {} Hörbüchern sind im Download-Katalog vorhanden",
                hoerbuchkatalog.anzahlDownloadbarerHoerbuecher(),
                hoerbuchkatalog.anzahlHoerbuecherGesamt());
    }

    private void istHoerbuchDownloadbar(final Hoerbuch hoerbuch, final Set<AghNummer> aghNummern) {
        boolean aghNummernVorhanden = !aghNummern.isEmpty();
        final AghNummer aghNummer = hoerbuch.getAghNummer();
        final boolean aghVorhanden = aghNummernVorhanden
                && null != hoerbuch.getAghNummer()
                && null != hoerbuch.getAghNummer().getValue();
        if (aghVorhanden) {
            final boolean downloadKatalogHatPassendeAghNummer =
                    null != aghNummer.getValue() && aghNummern.contains(aghNummer);
            LOGGER.trace("Suche AGH Nummer {} im Download-Katalog ergibt {}",
                    aghNummer, downloadKatalogHatPassendeAghNummer);
            if (downloadKatalogHatPassendeAghNummer) {
                hoerbuch.imDownloadKatalogVorhanden();
                LOGGER.trace("Hörbuch {} hat AGH Nummer {} und ist im Download-Katalog vorhanden",
                        hoerbuch.getTitelnummer(), hoerbuch.getAghNummer());
            } else {
                hoerbuch.nichtDownloadKatalogVorhanden();
                LOGGER.trace("Hörbuch {} hat keine AGH Nummer {} oder ist nicht im Download-Katalog vorhanden",
                        hoerbuch.getTitelnummer(), hoerbuch.getAghNummer());
            }
        }
    }

    private void archivRegelmaessigAktualisieren() {
        final CronTrigger cronTrigger = new CronTrigger(
                hoerbuchkatalogConfig.getHoerbuchkatalogCronExpression());
        LOGGER.info("Hörbuchkatalog wird regelmäßig aktualisiert, Cron={}",
                cronTrigger.getExpression());
        if (null != katalogeAktualisierenScheduledFuture) {
            katalogeAktualisierenScheduledFuture.cancel(true);
        }
        katalogeAktualisierenScheduledFuture = taskScheduler.schedule(
                this::aktualisiereArchiv, cronTrigger);
    }

    private void aktualisiereArchiv() {
        try {
            hoerbuchkatalogMapper.aktualisiereArchiv();
        } catch (HoerbuchkatalogArchivException e) {
            LOGGER.warn("Aktualisierung des WBH Hörbuchkatalogs nicht erfolgt: " + e.getMessage(), e);
        }
        try {
            aghNummernRepository.aktualisiereArchiv();
        } catch (HoerbuchkatalogArchivException e) {
            LOGGER.warn("Aktualisierung der AGH Nummern nicht erfolgt: " + e.getMessage(), e);
        }
        hoerbuchkatalog(true);
    }

    private Set<Hoerbuch> importiereHoerbuchkatalogAusArchiv(final Path gesamtDat) {
        Set<Hoerbuch> hoerbuecher = null;
        try {
            hoerbuecher = hoerbuchkatalogMapper.importiereAusArchiv(gesamtDat);
        } catch (HoerbuchkatalogArchivException e) {
            LOGGER.error("Unbekannter Fehler beim Importieren des Hörbuchkatalogs aus dem Archiv", e);
        }
        return null != hoerbuecher ? hoerbuecher : Collections.emptySet();
    }

    private Set<AghNummer> importiereAghNummernAusArchiv() {
        Set<AghNummer> aghNummern = null;
        try {
            aghNummern = aghNummernRepository.importiere();
        } catch (HoerbuchkatalogArchivException e) {
            LOGGER.error("Unbekannter Fehler beim Importieren der AGH Nummern aus dem Archiv", e);
        }
        return null != aghNummern ? aghNummern : Collections.emptySet();
    }

}
