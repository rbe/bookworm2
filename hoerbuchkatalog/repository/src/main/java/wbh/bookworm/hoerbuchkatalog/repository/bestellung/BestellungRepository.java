/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.repository.DomainRespositoryComponent;
import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;

@DomainRespositoryComponent
public class BestellungRepository extends JsonDomainRepository<Bestellung, BestellungId> {

    public BestellungRepository() {
        super(Bestellung.class, BestellungId.class);
    }

    public Bestellung erstellen(final Hoerernummer hoerernummer,
                                final String hoerername, final String hoereremail,
                                final String bemerkung,
                                final Boolean bestellkarteMischen, final Boolean alteBestellkarteLoeschen,
                                final WarenkorbId cdWarenkorbId, final WarenkorbId downloadWarenkorbId) {
        return new Bestellung(nextId("Hnr" + hoerernummer),
                hoerernummer,
                hoerername, hoereremail,
                bemerkung,
                bestellkarteMischen, alteBestellkarteLoeschen,
                cdWarenkorbId, downloadWarenkorbId);
    }

}
