/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.repository.DomainFactoryComponent;

import java.util.TreeSet;

@DomainFactoryComponent
public class WarenkorbFactory {

    public Warenkorb cdErstellen(final Hoerernummer hoerernummer) {
        return new CdWarenkorb(hoerernummer, new TreeSet<>());
    }

    public Warenkorb downloadErstellen(final Hoerernummer hoerernummer) {
        return new DownloadWarenkorb(hoerernummer, new TreeSet<>(),/*TODO*/5);
    }

}
