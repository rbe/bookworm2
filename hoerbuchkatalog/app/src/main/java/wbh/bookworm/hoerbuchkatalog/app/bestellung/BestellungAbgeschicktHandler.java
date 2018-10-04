/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.email.TemplateBuilder;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAbgeschickt;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;
import wbh.bookworm.platform.ddd.event.GlobalDomainEventSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class BestellungAbgeschicktHandler extends GlobalDomainEventSubscriber<BestellungAbgeschickt> {

    private final BestellungRepository bestellungRepository;

    private final TemplateBuilder templateBuilder;

    private final EmailService emailService;

    @Autowired
    public BestellungAbgeschicktHandler(final BestellungRepository bestellungRepository,
                                        final TemplateBuilder templateBuilder,
                                        final EmailService emailService) {
        super(BestellungAbgeschickt.class);
        logger.trace("Initializing");
        this.bestellungRepository = bestellungRepository;
        DomainEventPublisher.global().subscribe(this);
        this.templateBuilder = templateBuilder;
        this.emailService = emailService;
    }

    @Override
    public void handleEvent(final BestellungAbgeschickt domainEvent) {
        final Optional<Bestellung> bestellung =
                bestellungRepository.load(domainEvent.getBestellungId());
        bestellung.ifPresent(best ->
                logger.info("Hörer {}, BestellungId {}, CdWarenkorbId {}, DownloadWarenkorbId {}," +
                        " we should implement sending an email to {}...",
                domainEvent.getHoerernummer(),
                domainEvent.getBestellungId(),
                domainEvent.getCdWarenkorbId(), domainEvent.getDownloadWarenkorbId(),
                best.getHoereremail()));
    }

}
