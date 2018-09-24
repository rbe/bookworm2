/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;

import java.util.TreeSet;

public final class Merkliste extends Warenkorb {

    public Merkliste(final Hoerernummer hoerernummer) {
        super(hoerernummer, new TreeSet<>());
    }

    @Override
    public void hinzufuegen(final Titelnummer titelnummer) {
        super.hinzufuegen(titelnummer);
        DomainEventPublisher.global()
                .publish(new HoerbuechAufDieMerklisteGesetzt(hoerernummer, titelnummer));
    }

    @Override
    public void entfernen(final Titelnummer titelnummer) {
        super.entfernen(titelnummer);
        DomainEventPublisher.global()
                .publish(new HoerbuechVonDerMerklisteEntfernt(hoerernummer, titelnummer));
    }

    public void bestellen() {
        throw new UnsupportedOperationException("Eine Merkliste kann nicht bestellt werden");
    }

}
