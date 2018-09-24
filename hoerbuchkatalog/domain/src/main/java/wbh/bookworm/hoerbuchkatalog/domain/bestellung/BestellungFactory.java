/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.repository.DomainFactoryComponent;

@DomainFactoryComponent
public class BestellungFactory {

    public Bestellung erstellen(final String hoerername, final Hoerernummer hoerernummer,
                                final String hoereremail, final String bemerkung,
                                final Boolean bestellkarteMischen, final Boolean alteBestellkarteLoeschen,
                                final CdWarenkorb cdWarenkorb, final DownloadWarenkorb downloadWarenkorb) {
        return new Bestellung(hoerername, hoerernummer, hoereremail,
                bemerkung, bestellkarteMischen, alteBestellkarteLoeschen,
                cdWarenkorb, downloadWarenkorb);
    }

}
