/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import wbh.bookworm.hoerbuchkatalog.domain.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuchkatalog;
import wbh.bookworm.hoerbuchkatalog.domain.Titelnummer;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
final class HoerbuchkatalogFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogFactory.class);

    private final RepositoryConfig repositoryConfig;

    private final RepositoryArchiv repositoryArchiv;

    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> katalogeImportierenScheduledFuture;

    private final HoerbuchkatalogImporter hoerbuchkatalogImporter;

    private final AghNummernImporter aghNummernImporter;

    private final Map<Titelnummer, Hoerbuch> katalog;

    private Hoerbuchkatalog hoerbuchkatalog;

    private final HoerbuchkatalogSuche hoerbuchkatalogSuche;

    @Autowired
    HoerbuchkatalogFactory(final RepositoryConfig repositoryConfig,
                           final RepositoryArchiv repositoryArchiv,
                           final TaskScheduler taskScheduler,
                           final HoerbuchkatalogImporter hoerbuchkatalogImporter,
                           final AghNummernImporter aghNummernImporter,
                           final Map<Titelnummer, Hoerbuch> katalog,
                           final HoerbuchkatalogSuche hoerbuchkatalogSuche) {
        this.repositoryConfig = repositoryConfig;
        this.repositoryArchiv = repositoryArchiv;
        this.taskScheduler = taskScheduler;
        this.hoerbuchkatalogImporter = hoerbuchkatalogImporter;
        this.aghNummernImporter = aghNummernImporter;
        this.katalog = katalog;
        this.hoerbuchkatalogSuche = hoerbuchkatalogSuche;
    }

    @Bean
    Hoerbuchkatalog hoerbuchkatalog() {
        return hoerbuchkatalog(false);
    }

    void aktualisiereArchiv() {
        try {
            hoerbuchkatalogImporter.aktualisiereKatalogImArchiv();
        } catch (ImportFailedException e) {
            LOGGER.warn("Aktualisierung des WBH Hörbuchkatalogs nicht erfolgt: {}", e.getMessage());
            LOGGER.debug("Aktualisierung des WBH Hörbuchkatalogs nicht erfolgt", e);
        }
        try {
            aghNummernImporter.aktualisiereKatalogImArchiv();
        } catch (ImportFailedException e) {
            LOGGER.warn("Aktualisierung der AGH Nummern nicht erfolgt: {}", e.getMessage());
            LOGGER.debug("Aktualisierung der AGH Nummern nicht erfolgt", e);
        }
        hoerbuchkatalog(true);
    }

    void archivRegelmaessigAktualisieren() {
        final CronTrigger katalogeImportierenCronTrigger =
                new CronTrigger(repositoryConfig.getHoerbuchkatalogCronExpression());
        LOGGER.info("Hörbuchkatalog wird regelmäßig aktualisiert, Cron={}",
                katalogeImportierenCronTrigger.getExpression());
        if (null != katalogeImportierenScheduledFuture) {
            katalogeImportierenScheduledFuture.cancel(true);
        }
        katalogeImportierenScheduledFuture =
                taskScheduler.schedule(this::aktualisiereArchiv, katalogeImportierenCronTrigger);
    }

    private Set<Hoerbuch> importiereHoerbuchkatalogAusArchiv() {
        Set<Hoerbuch> hoerbuecher = null;
        try {
            hoerbuecher = hoerbuchkatalogImporter.importiereKatalogAusArchiv();
        } catch (ImportFailedException e) {
            LOGGER.error("Unbekannter Fehler beim Importieren des Hörbuchkatalogs aus dem Archiv", e);
        }
        return null != hoerbuecher ? hoerbuecher : Collections.emptySet();
    }

    private Set<AghNummer> importiereAghNummernAusArchiv() {
        Set<AghNummer> aghNummern = null;
        try {
            aghNummern = aghNummernImporter.importiereKatalogAusArchiv();
        } catch (ImportFailedException e) {
            LOGGER.error("Unbekannter Fehler beim Importieren der AGH Nummern aus dem Archiv", e);
        }
        return null != aghNummern ? aghNummern : Collections.emptySet();
    }

    private Hoerbuchkatalog hoerbuchkatalog(final boolean neu) {
        if (null == hoerbuchkatalog || neu) {
            LOGGER.info("Erzeuge neuen Hörbuchkatalog");
            katalog.clear();
            final Hoerbuchkatalog hoerbuchkatalog = new Hoerbuchkatalog(katalog);
            repositoryArchiv.createDirectoryOrFail(repositoryConfig.getHoerbuchkatalogDirectory());
            final Set<Hoerbuch> hoerbuecher = importiereHoerbuchkatalogAusArchiv();
            final Set<AghNummer> aghNummern = importiereAghNummernAusArchiv();
            boolean aghNummernVorhanden = !aghNummern.isEmpty();
            hoerbuecher.forEach(hoerbuch -> {
                final boolean aghVorhanden = aghNummernVorhanden && null != hoerbuch.getAghNummer();
                if (aghVorhanden) {
                    hoerbuchDownloadbar(hoerbuch, aghNummern);
                }
                hoerbuchkatalog.hinzufuegen(hoerbuch);
            });
            LOGGER.info("{} von {} Hörbüchern sind im Download-Katalog vorhanden",
                    hoerbuchkatalog.anzahlDownloadbarerHoerbuecher(),
                    hoerbuchkatalog.anzahlHoerbuecher());
            try {
                hoerbuchkatalogSuche.indexLoeschen();
                hoerbuchkatalogSuche.indexAufbauen(hoerbuchkatalog.alleHoerbuecher());
            } catch (IOException e) {
                throw new RuntimeException("Kann Hörbuchsuche nicht initialisieren", e);
            }
            LOGGER.trace("Hörbuchkatalog erzeugt, {}", this);
            this.hoerbuchkatalog = hoerbuchkatalog;
        }
        LOGGER.trace("Hörbuchkatalog bereits erzeugt, {}", this);
        return hoerbuchkatalog;
    }

    private void hoerbuchDownloadbar(final Hoerbuch hoerbuch, final Set<AghNummer> aghNummern) {
        final AghNummer aghNummer = hoerbuch.getAghNummer();
        final boolean downloadKatalogHatPassendeAghNummer = null != aghNummer && aghNummern.contains(aghNummer);
        LOGGER.trace("Suche AGH Nummer {} im Download-Katalog: {}", aghNummer, downloadKatalogHatPassendeAghNummer);
        if (downloadKatalogHatPassendeAghNummer) {
            hoerbuch.imDownloadKatalogVorhanden();
            LOGGER.trace("Hörbuch {} hat AGH Nummer {}; ist im Download-Katalog vorhanden",
                    hoerbuch.getTitelnummer(), hoerbuch.getAghNummer());
        } else {
            hoerbuch.nichtDownloadKatalogVorhanden();
            LOGGER.trace("Hörbuch {} hat keine AGH Nummer {}; oder ist nicht im Download-Katalog vorhanden",
                    hoerbuch.getTitelnummer(), hoerbuch.getAghNummer());
        }
    }

}
