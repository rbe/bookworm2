/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailTemplateBuilder;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import aoc.ddd.event.DomainEventPublisher;
import aoc.ddd.event.DomainEventSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CdBestellungAufgegebenHandler extends DomainEventSubscriber<BestellungAufgegeben> {

    private final Hoerbuchkatalog hoerbuchkatalog;

    private final EmailTemplateBuilder emailTemplateBuilder;

    private final EmailService emailService;

    @Autowired
    public CdBestellungAufgegebenHandler(final Hoerbuchkatalog hoerbuchkatalog,
                                         final EmailTemplateBuilder emailTemplateBuilder,
                                         final EmailService emailService) {
        super(BestellungAufgegeben.class);
        logger.trace("Initializing");
        this.hoerbuchkatalog = hoerbuchkatalog;
        this.emailTemplateBuilder = emailTemplateBuilder;
        this.emailService = emailService;
        DomainEventPublisher.global().subscribe(this);
    }

    @Override
    public void handleEvent(final BestellungAufgegeben domainEvent) {
        final Bestellung bestellung = (Bestellung) domainEvent.getDomainAggregate();
        final BestellungId bestellungId = domainEvent.getDomainId();
        logger.info("Bestellung {}: Hörer {} hat folgende CDs bestellt: {}",
                bestellungId, domainEvent.getHoerernummer(), bestellung.getCdTitelnummern());
        final Set<Hoerbuch> hoerbucher = bestellung.getCdTitelnummern().stream().
                map(hoerbuchkatalog::hole)
                .collect(Collectors.toSet());
        final String htmlEmail = emailTemplateBuilder.build("BestellbestaetigungCd.html",
                Map.of("bestellung", bestellung, "hoerbuecher", hoerbucher));
        emailService.send(bestellung.getHoereremail().getValue(), "wbh@wbh-online.de",
                "Ihre Bestellung bei der WBH",
                htmlEmail);
    }

}
