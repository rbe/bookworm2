/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import java.util.Optional;
import java.util.Set;

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

    public Optional<Set<Downloads>> alle() {
        return downloadsRepository.loadAll();
    }

    public Downloads downloadsKopie(final Hoerernummer hoerernummer) {
        return new Downloads(downloads(hoerernummer));
    }

    private Downloads downloads(final Hoerernummer hoerernummer) {
        return downloadsRepository.load(hoerernummer)
                .orElseGet(() -> downloadsRepository.erstellen(hoerernummer));
    }

    public long anzahlHeute(final Hoerernummer hoerernummer) {
        return downloads(hoerernummer).anzahlHeute();
    }

    public long anzahlAusleihzeitraum(final Hoerernummer hoerernummer) {
        return downloads(hoerernummer).anzahlAusleihzeitraum();
    }

    public boolean neuerDownloadErlaubt(final Hoerernummer hoerernummer) {
        return downloads(hoerernummer).neuerDownloadErlaubt();
    }

    public boolean downloadErlaubt(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        return downloads(hoerernummer).downloadErlaubt(titelnummer);
    }

    public int anzahlDownloads(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        return downloads(hoerernummer).anzahlDownloads(titelnummer).orElse(-1);
    }

    /**
     * Command
     */
    public boolean ausleihen(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        final Downloads downloads = downloads(hoerernummer);
        final boolean ausgeliehen = downloads.ausleihen(titelnummer);
        //if (downloads.imAusleihzeitraumEnthalten(titelnummer)) {
        downloadsRepository.save(downloads);
        return ausgeliehen;
        /*} else {
            return false;
        }*/
    }

    public boolean enthalten(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        return downloads(hoerernummer).imAusleihzeitraumEnthalten(titelnummer);
    }

}
