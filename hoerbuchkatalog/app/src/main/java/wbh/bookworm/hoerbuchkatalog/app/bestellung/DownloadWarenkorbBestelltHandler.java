/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorbBestellt;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;
import wbh.bookworm.platform.ddd.event.GlobalDomainEventSubscriber;

import org.springframework.stereotype.Component;

@Component
public class DownloadWarenkorbBestelltHandler extends GlobalDomainEventSubscriber<CdWarenkorbBestellt> {

    public DownloadWarenkorbBestelltHandler() {
        super(CdWarenkorbBestellt.class);
        logger.trace("Initializing");
        DomainEventPublisher.global().subscribe(this);
    }

    @Override
    public void handleEvent(final CdWarenkorbBestellt domainEvent) {
        logger.info("Hörer {} hat folgende Downloads bestellt: {}",
                domainEvent.getHoerernummer(), domainEvent.getTitelnummern());
    }

}
