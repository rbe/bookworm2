/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.repository.DomainFactoryComponent;

@DomainFactoryComponent
public class MerklisteFactory {

    public Merkliste erstellen(final Hoerernummer hoerernummer) {
        return new Merkliste(hoerernummer);
    }

}
