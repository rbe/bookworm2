/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.lieferung;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.CdLieferungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten.HoererRepository;
import wbh.bookworm.shared.domain.Hoerernummer;

@Service
public final class CdLieferungService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdLieferungService.class);

    private final RepositoryResolver repositoryResolver;

    @Autowired
    public CdLieferungService(final RepositoryResolver repositoryResolver) {
        LOGGER.trace("Initializing");
        this.repositoryResolver = repositoryResolver;
    }

    public boolean hatBelastungen(final Hoerernummer hoerernummer) {
        final HoererRepository hoererRepository = repositoryResolver.hoererRepository();
        return !hoererRepository.belastungen(hoerernummer).isEmpty();
    }

    public List<Belastung> belastungen(final Hoerernummer hoerernummer) {
        /* TODO Security Context */if (hoerernummer.isUnbekannt()) {
            return Collections.emptyList();
        } else {
            final HoererRepository hoererRepository = repositoryResolver.hoererRepository();
            final List<Belastung> belastungen = hoererRepository.belastungen(hoerernummer);
            belastungen.sort(Comparator.nullsFirst(
                    Comparator.comparing(Belastung::getBelastungsdatum,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .reversed());
            LOGGER.info("{} Belastungen für Hörer {} gefunden", belastungen.size(), hoerernummer);
            return belastungen;
        }
    }

    public boolean hatBestellkarten() {
        final CdLieferungRepository cdLieferungRepository = repositoryResolver.cdLieferungRepository();
        return cdLieferungRepository.hatBestellkarten();
    }

    public List<Bestellkarte> bestellkarten(final Hoerernummer hoerernummer) {
        /* TODO Security Context */if (hoerernummer.isUnbekannt()) {
            return Collections.emptyList();
        } else {
            final CdLieferungRepository cdLieferungRepository = repositoryResolver.cdLieferungRepository();
            final List<Bestellkarte> bestellkarten = cdLieferungRepository.bestellkarten(hoerernummer);
            /*bestellkarten.sort(Comparator.nullsFirst(
                    // TODO ::getTitelnummer, Comparable<Titelnummer>
                    Comparator.comparing(Bestellkarte::toString,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .reversed());*/
            LOGGER.info("{} Bestellkarten für Hörer {} gefunden und sortiert",
                    bestellkarten.size(), hoerernummer);
            return bestellkarten;
        }
    }

    public boolean hatErledigteBestellkarten() {
        final CdLieferungRepository cdLieferungRepository = repositoryResolver.cdLieferungRepository();
        return cdLieferungRepository.hatErledigteBestellkarten();
    }

    public List<ErledigteBestellkarte> erledigteBestellkarten(final Hoerernummer hoerernummer) {
        /* TODO Security Context */if (hoerernummer.isUnbekannt()) {
            return Collections.emptyList();
        } else {
            final CdLieferungRepository cdLieferungRepository = repositoryResolver.cdLieferungRepository();
            final List<ErledigteBestellkarte> erledigteBestellkarten =
                    cdLieferungRepository.erledigteBestellkarten(hoerernummer);
            erledigteBestellkarten.sort(Comparator.nullsFirst(
                    Comparator.comparing(ErledigteBestellkarte::getAusleihdatum,
                            Comparator.nullsFirst(Comparator.naturalOrder())))
                    .reversed());
            LOGGER.info("{} erledigte Bestellkarten für Hörer {} gefunden und sortiert",
                    erledigteBestellkarten.size(), hoerernummer);
            return erledigteBestellkarten;
        }
    }

}
