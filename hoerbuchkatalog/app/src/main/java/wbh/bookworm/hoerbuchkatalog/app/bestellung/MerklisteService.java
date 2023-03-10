/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.MerklisteRepository;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

@Service
public class MerklisteService {

    private final MerklisteRepository merklisteRepository;

    @Autowired
    public MerklisteService(final MerklisteRepository merklisteRepository) {
        this.merklisteRepository = merklisteRepository;
    }

    public Merkliste merklisteKopie(final Hoerernummer hoerernummer) {
        return new Merkliste(merkliste(hoerernummer));
    }

    private Merkliste merkliste(final Hoerernummer hoerernummer) {
        return merklisteRepository.load(hoerernummer)
                .orElseGet(() -> merklisteRepository.erstellen(hoerernummer));
    }

    public int anzahl(final Hoerernummer hoerernummer) {
        return merkliste(hoerernummer).getAnzahl();
    }

    public Set<Titelnummer> titelnummernAufMerkliste(final Hoerernummer hoerernummer) {
        return merkliste(hoerernummer).getTitelnummern();
    }

    /**
     * Command
     */
    public boolean hinzufuegen(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        final Merkliste merkliste = merkliste(hoerernummer);
        merkliste.hinzufuegen(titelnummer);
        if (merkliste.enthalten(titelnummer)) {
            merklisteRepository.save(merkliste);
            return true;
        } else {
            return false;
        }
    }

    public boolean enthalten(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        return merkliste(hoerernummer).enthalten(titelnummer);
    }

    /**
     * Command
     * @return
     */
    public boolean entfernen(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        final Merkliste merkliste = merkliste(hoerernummer);
        merkliste.entfernen(titelnummer);
        if (!merkliste.enthalten(titelnummer)) {
            merklisteRepository.save(merkliste);
            return true;
        } else {
            return false;
        }
    }

}
