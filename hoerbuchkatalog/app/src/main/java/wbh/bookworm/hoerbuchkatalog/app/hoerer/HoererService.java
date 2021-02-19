/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.hoerer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.DownloadsRepository;
import wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten.HoererProfilRepository;
import wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten.HoererRepository;
import wbh.bookworm.shared.domain.Hoerernummer;

@Service
public final class HoererService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererService.class);

    private final HoererRepository hoererRepository;

    private final HoererProfilRepository hoererProfilRepository;

    private final DownloadsRepository downloadsRepository;

    @Autowired
    public HoererService(final HoererRepository hoererRepository,
                         final HoererProfilRepository hoererProfilRepository,
                         final DownloadsRepository downloadsRepository) {
        this.hoererRepository = hoererRepository;
        this.hoererProfilRepository = hoererProfilRepository;
        this.downloadsRepository = downloadsRepository;
    }

    public Hoerer hoerer(final Hoerernummer hoerernummer) {
        /* TODO Security Context */
        if (hoerernummer.isUnbekannt()) {
            return Hoerer.UNBEKANNT;
        } else {
            final Optional<Hoerer> hoerer = hoererRepository.hoerer(hoerernummer);
            if (hoerer.isPresent()) {
                if (hoerer.get().isBekannt()) {
                    LOGGER.debug("Hörer '{}': Hörerdaten gefunden", hoerernummer);
                    return hoerer.get();
                } else {
                    LOGGER.warn("Hörer '{}': Keine Hörerdaten gefunden", hoerernummer);
                }
            }
        }
        return Hoerer.UNBEKANNT;
    }

    public int anzahlBestellungenProAusleihzeitraum(final Hoerernummer hoerernummer) {
        return downloadsRepository.load(hoerernummer)
                .orElse(downloadsRepository.erstellen(hoerernummer))
                .getAnzahlBestellungenProAusleihzeitraum();
    }

    public void neueAnzahlBestellungenProAusleihzeitraum(final Hoerernummer hoerernummer, int anzahlBestellungen) {
        final Downloads downloads = downloadsRepository.load(hoerernummer)
                .orElse(downloadsRepository.erstellen(hoerernummer));
        downloads.setAnzahlBestellungenProAusleihzeitraum(anzahlBestellungen);
        downloadsRepository.save(downloads);
    }

    public int anzahlBestellungenProTag(final Hoerernummer hoerernummer) {
        return downloadsRepository.load(hoerernummer)
                .orElse(downloadsRepository.erstellen(hoerernummer))
                .getAnzahlBestellungenProTag();
    }

    public void neueAnzahlBestellungenProTag(final Hoerernummer hoerernummer, int anzahlBestellungen) {
        final Downloads downloads = downloadsRepository.load(hoerernummer)
                .orElse(downloadsRepository.erstellen(hoerernummer));
        downloads.setAnzahlBestellungenProTag(anzahlBestellungen);
        downloadsRepository.save(downloads);
    }

    public int anzahlDownloadsProHoerbuch(final Hoerernummer hoerernummer) {
        return downloadsRepository.load(hoerernummer)
                .orElse(downloadsRepository.erstellen(hoerernummer))
                .getAnzahlDownloadsProHoerbuch();
    }

    public void neueAnzahlDownloadsProHoerbuch(final Hoerernummer hoerernummer, final int anzahlDownloadsProHoerbuch) {
        final Downloads downloads = downloadsRepository.load(hoerernummer)
                .orElse(downloadsRepository.erstellen(hoerernummer));
        downloads.setAnzahlDownloadsProHoerbuch(anzahlDownloadsProHoerbuch);
        downloadsRepository.save(downloads);
    }

    public void neueAnzahlBestellungenProAusleihzeitraum(int anzahlBestellungen) {
        neueAnzahlBestellungenProAusleihzeitraum(Hoerernummer.UNBEKANNT, anzahlBestellungen);
    }

    public void neueAnzahlBestellungenProTag(int anzahlBestellungen) {
        neueAnzahlBestellungenProTag(Hoerernummer.UNBEKANNT, anzahlBestellungen);
    }

    public void neueAnzahlDownloadsProHoerbuch(final int anzahlDownloadsProHoerbuch) {
        neueAnzahlDownloadsProHoerbuch(Hoerernummer.UNBEKANNT, anzahlDownloadsProHoerbuch);
    }

}
