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
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;
import aoc.mikrokosmos.ddd.event.DomainEventSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class CdBestellungAufgegebenHandler extends DomainEventSubscriber<BestellungAufgegeben> {

    private final RepositoryResolver repositoryResolver;

    private final EmailTemplateBuilder emailTemplateBuilder;

    private final EmailService emailService;

    @Autowired
    CdBestellungAufgegebenHandler(final RepositoryResolver repositoryResolver,
                                  final EmailTemplateBuilder emailTemplateBuilder,
                                  final EmailService emailService) {
        super(BestellungAufgegeben.class);
        logger.trace("Initializing");
        this.repositoryResolver = repositoryResolver;
        this.emailTemplateBuilder = emailTemplateBuilder;
        this.emailService = emailService;
        DomainEventPublisher.global().subscribe(this);
    }

    @Override
    public void handleEvent(final BestellungAufgegeben domainEvent) {
        final Bestellung bestellung = (Bestellung) domainEvent.getDomainAggregate();
        final BestellungId bestellungId = domainEvent.getDomainId();
        logger.info("Bestellung '{}': Hörer '{}' hat folgende CDs bestellt: {}",
                bestellungId, domainEvent.getHoerernummer(), bestellung.getCdTitelnummern());
        final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
        final Set<Hoerbuch> hoerbucher = bestellung.getCdTitelnummern().stream().
                map(hoerbuchkatalog::hole)
                .collect(Collectors.toSet());
        if (!hoerbucher.isEmpty()) {
            emailErzeugenArchivierenUndVersenden(domainEvent, bestellung, hoerbucher);
        }
    }

    private void emailErzeugenArchivierenUndVersenden(final BestellungAufgegeben domainEvent,
                                                      final Bestellung bestellung,
                                                      final Set<Hoerbuch> hoerbucher) {
        final String htmlEmail = emailTemplateBuilder.build(
                "BestellbestaetigungCd.html",
                Map.of("bestellung", bestellung, "hoerbuecher", hoerbucher));
        // TODO emailArchivieren(domainEvent, htmlEmail);
        emailService.send(bestellung.getHoereremail().getValue(), "wbh@wbh-online.de",
                "Ihre CD-Bestellung bei der WBH",
                htmlEmail);
    }

    private void emailArchivieren(final BestellungAufgegeben domainEvent,
                                  final String htmlEmail) {
        logger.trace("Hörer '{}': Archiviere E-Mail zu Bestellung '{}'",
                domainEvent.getHoerernummer(), domainEvent.getDomainId());
        try {
            final Path archivDatei = Path.of("/var/local/repository/Bestellung",
                    domainEvent.getDomainId() + "-CDBestellung.html");
            Files.createDirectories(archivDatei.getParent());
            Files.write(archivDatei, htmlEmail.getBytes(StandardCharsets.UTF_8));
            logger.info("Hörer '{}': E-Mail zu Bestellung '{}' unter '{}' archiviert",
                    domainEvent.getHoerernummer(), domainEvent.getDomainId(), archivDatei);
        } catch (IOException e) {
            logger.error(String.format(
                    "Hörer %s: Kann E-Mail für Bestellung %s nicht archivieren:%n%s",
                    domainEvent.getHoerernummer(), domainEvent.getDomainId(), htmlEmail),
                    e);
        }
    }

}
