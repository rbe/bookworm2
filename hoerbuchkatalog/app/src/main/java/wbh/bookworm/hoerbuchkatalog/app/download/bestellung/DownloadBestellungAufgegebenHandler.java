/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.download.bestellung;

import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailTemplateBuilder;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import aoc.ddd.event.DomainEventPublisher;
import aoc.ddd.event.DomainEventSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DownloadBestellungAufgegebenHandler extends DomainEventSubscriber<BestellungAufgegeben> {

    private final BestellungRepository bestellungRepository;

    private final Hoerbuchkatalog hoerbuchkatalog;

    private final EmailTemplateBuilder emailTemplateBuilder;

    private final EmailService emailService;

    @Autowired
    public DownloadBestellungAufgegebenHandler(final BestellungRepository bestellungRepository,
                                               final Hoerbuchkatalog hoerbuchkatalog,
                                               final EmailTemplateBuilder emailTemplateBuilder,
                                               final EmailService emailService) {
        super(BestellungAufgegeben.class);
        logger.trace("Initializing");
        this.bestellungRepository = bestellungRepository;
        this.hoerbuchkatalog = hoerbuchkatalog;
        this.emailTemplateBuilder = emailTemplateBuilder;
        this.emailService = emailService;
        DomainEventPublisher.global().subscribe(this);
    }

    @Override
    public void handleEvent(final BestellungAufgegeben domainEvent) {
        final Hoerernummer hoerernummer = domainEvent.getHoerernummer();
        final Bestellung bestellung = (Bestellung) domainEvent.getDomainAggregate();
        final Set<Titelnummer> downloadTitelnummern = bestellung.getDownloadTitelnummern();
        final AghNummer[] aghNummern = downloadTitelnummern.stream()
                .map(tn -> hoerbuchkatalog.hole(tn).getAghNummer())
                .toArray(AghNummer[]::new);
        logger.info("Hörer {} hat folgende Downloads bestellt: {}", hoerernummer, aghNummern);
        // TODO blista
    }

}
