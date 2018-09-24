/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.platform.ddd.repository.DomainRespositoryComponent;
import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;

@DomainRespositoryComponent
public class BestellungRepository extends JsonDomainRepository<Bestellung> {

    public BestellungRepository() {
        super(Bestellung.class);
    }

}
