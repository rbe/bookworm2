/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.HoerbuchkatalogAktualisiert;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.HoerbuchkatalogId;
import wbh.bookworm.shared.domain.AghNummer;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;
import aoc.mikrokosmos.ddd.model.DomainId;
import aoc.mikrokosmos.ddd.repository.DomainRepositoryComponent;

@Configuration
@Component
@DomainRepositoryComponent
public class HoerbuchkatalogRepository/* TODO extends JsonDomainRepository<Hoerbuchkatalog, HoerbuchkatalogId>*/ {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogRepository.class);

    private final ApplicationContext applicationContext;

    private final HoerbuchkatalogConfig hoerbuchkatalogConfig;

    private final Path wbhKatalogDateiname;

    private final HoerbuchkatalogMapper hoerbuchkatalogMapper;

    private final HoerbuchkatalogArchiv hoerbuchkatalogArchiv;

    private final AtomicReference<Hoerbuchkatalog> aktuellerHoerbuchkatalog;

    @Autowired
    HoerbuchkatalogRepository(final ApplicationContext applicationContext,
                              final HoerbuchkatalogConfig hoerbuchkatalogConfig,
                              final HoerbuchkatalogArchiv hoerbuchkatalogArchiv) {
        /* TODO super(Hoerbuchkatalog.class, HoerbuchkatalogId.class, hoerbuchkatalogConfig.getDirectory());*/
        this.applicationContext = applicationContext;
        this.hoerbuchkatalogConfig = hoerbuchkatalogConfig;
        wbhKatalogDateiname = Path.of(hoerbuchkatalogConfig.getWbhGesamtdatFilename());
        this.hoerbuchkatalogMapper = new HoerbuchkatalogMapper();
        this.hoerbuchkatalogArchiv = hoerbuchkatalogArchiv;
        this.aktuellerHoerbuchkatalog = new AtomicReference<>();
        datenEinlesen();
    }

    @Bean
    @Lazy
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Hoerbuchkatalog hoerbuchkatalog() {
        final Hoerbuchkatalog hoerbuchkatalog = aktuellerHoerbuchkatalog.get();
        LOGGER.trace("Gebe {} zurück", hoerbuchkatalog);
        return hoerbuchkatalog;
    }

    /**
     * {@link org.springframework.scheduling.annotation.Scheduled},
     * weil Aktualisierung AGH Nummern seitens blista morgens um 5 Uhr
     * TODO Logik nach GesamtDatRepository#load("Datum") inkl. Selektion bestehender LuceneIndex
     */
    @Scheduled(cron = "0 15 5 * * MON-FRI")
    synchronized void datenEinlesen() {
        LOGGER.info("Baue neuen Hörbuchkatalog aus Gesamt.dat auf");
        final Set<AghNummer> aghNummern = Collections.emptySet();//importiereAghNummernAusArchiv();
        final Set<Hoerbuch> hoerbuecher = importiereHoerbuchkatalogAusArchiv();
        LOGGER.trace("Baue neuen Hörbuchkatalog auf");
        if (hoerbuecher.isEmpty()) {
            aktuellerHoerbuchkatalog.set(Hoerbuchkatalog.leererHoerbuchkatalog());
            LOGGER.warn("Import leer; Keine Hörbücher importiert");
        } else {
            baueHoerbuchkatalogAuf(hoerbuecher, aghNummern);
        }
        LOGGER.info("Hörbuchkatalog mit AGH Nummern erfolgreich aufgebaut");
    }

    private void baueHoerbuchkatalogAuf(final Set<Hoerbuch> hoerbuecher,
                                        final Set<AghNummer> aghNummern) {
        final long neueVersion = null != aktuellerHoerbuchkatalog.get()
                ? aktuellerHoerbuchkatalog.get().getVersion() + 1
                : 1;
        final HoerbuchkatalogId id = new HoerbuchkatalogId(UUID.randomUUID().toString());
        final Hoerbuchkatalog neuerKatalog = new Hoerbuchkatalog(id, neueVersion);
        verheirate(neuerKatalog, hoerbuecher, aghNummern);
        LOGGER.debug("Hörbuchkatalog {} erfolgreich erzeugt", id);
        initialisiereSuche(id, neuerKatalog);
        aktuellerHoerbuchkatalog.set(neuerKatalog);
        DomainEventPublisher.global().publishAsync(new HoerbuchkatalogAktualisiert(
                id, neueVersion));
    }

    private void verheirate(final Hoerbuchkatalog hoerbuchkatalog,
                            final Set<Hoerbuch> hoerbuecher,
                            final Set<AghNummer> aghNummern) {
        LOGGER.trace("Verheirate {} WBH Titelnummern und {} blista AGH Nummern",
                hoerbuecher.size(), aghNummern.size());
        hoerbuecher.forEach(hoerbuch -> {
            isHoerbuchDownloadbar(hoerbuch, aghNummern);
            hoerbuchkatalog.hinzufuegen(hoerbuch);
        });
        LOGGER.info("{} downloadbare Hörbücher (insgesamt {} Hörbüchern und {} AGH Nummern)",
                hoerbuchkatalog.anzahlDownloadbarerHoerbuecher(),
                hoerbuchkatalog.anzahlHoerbuecherGesamt(),
                aghNummern.size());
    }

    private void isHoerbuchDownloadbar(final Hoerbuch hoerbuch,
                                       final Set<AghNummer> aghNummern) {
        LOGGER.trace("Suche AGH Nummer {} von Hörbuch {} im Download-Katalog",
                hoerbuch.getAghNummer(), hoerbuch.getTitelnummer());
        boolean aghNummernVorhanden = !aghNummern.isEmpty();
        final AghNummer aghNummer = hoerbuch.getAghNummer();
        final boolean aghVorhanden = aghNummernVorhanden
                && null != hoerbuch.getAghNummer()
                && null != hoerbuch.getAghNummer().getValue();
        if (aghVorhanden) {
            final boolean downloadKatalogHatPassendeAghNummer =
                    null != aghNummer.getValue() && aghNummern.contains(aghNummer);
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

    private Set<Hoerbuch> importiereHoerbuchkatalogAusArchiv() {
        Set<Hoerbuch> hoerbuecher = null;
        hoerbuchkatalogArchiv.archiviereNeuenKatalog(wbhKatalogDateiname);
        final Optional<Path> gesamtDat = hoerbuchkatalogArchiv.findeAktuellstenKatalog(wbhKatalogDateiname);
        if (gesamtDat.isPresent()) {
            final Path path = gesamtDat.get();
            LOGGER.trace("Erzeuge neuen Hörbuchkatalog aus '{}'", path);
            try {
                hoerbuecher = hoerbuchkatalogMapper.importiere(path,
                        hoerbuchkatalogConfig.getWbhGesamtdatCharset());
            } catch (HoerbuchkatalogArchivException e) {
                LOGGER.error("Unbekannter Fehler beim Importieren des Hörbuchkatalogs aus dem Archiv", e);
            }
        } else {
            LOGGER.error("Es wurde kein Hörbuchkatalog in '{}/{}' gefunden",
                    hoerbuchkatalogConfig.getDirectory().toAbsolutePath(), wbhKatalogDateiname);
        }
        return null != hoerbuecher ? hoerbuecher : Collections.emptySet();
    }

    private void initialisiereSuche(final DomainId<String> hoerbuchkatalogId,
                                    final Hoerbuchkatalog hoerbuchkatalog) {
        LOGGER.trace("Initialisiere Suchindex für Hörbuchkatalog {}", hoerbuchkatalogId);
        final HoerbuchkatalogSuche hoerbuchkatalogSuche = new HoerbuchkatalogSuche(
                applicationContext, hoerbuchkatalogId,
                hoerbuchkatalogConfig.getAnzahlSuchergebnisse());
        // TODO Nur on-demand, wenn kein Index besteht
        hoerbuchkatalogSuche.indiziere(hoerbuchkatalog.alle());
        hoerbuchkatalog.setHoerbuchkatalogSuche(hoerbuchkatalogSuche);
        LOGGER.debug("Suchindex für Hörbuchkatalog {} aufgebaut", hoerbuchkatalogId);
    }

}
