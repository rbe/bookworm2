/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.DownloadsRepository;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Service
public class DownloadsService {

    private final DownloadsRepository downloadsRepository;

    @Autowired
    public DownloadsService(final DownloadsRepository downloadsRepository) {
        this.downloadsRepository = downloadsRepository;
    }

    public Downloads downloadsKopie(final Hoerernummer hoerernummer) {
        return new Downloads(downloads(hoerernummer));
    }

    private Downloads downloads(final Hoerernummer hoerernummer) {
        return downloadsRepository.load(hoerernummer)
                .orElseGet(() -> downloadsRepository.erstellen(hoerernummer));
    }

    public int anzahl(final Hoerernummer hoerernummer) {
        return downloads(hoerernummer).getAnzahl();
    }

    /**
     * Command
     */
    public boolean hinzufuegen(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        final Downloads downloads = downloads(hoerernummer);
        downloads.hinzufuegen(titelnummer);
        if (downloads.enthalten(titelnummer)) {
            downloadsRepository.save(downloads);
            return true;
        } else {
            return false;
        }
    }

    public boolean enthalten(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        return downloads(hoerernummer).enthalten(titelnummer);
    }

}
