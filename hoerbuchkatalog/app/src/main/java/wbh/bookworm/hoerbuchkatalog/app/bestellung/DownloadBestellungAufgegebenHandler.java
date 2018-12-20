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
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung.Auftragsquittung;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung.DlsBestellung;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import aoc.ddd.event.DomainEventPublisher;
import aoc.ddd.event.DomainEventSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DownloadBestellungAufgegebenHandler extends DomainEventSubscriber<BestellungAufgegeben> {

    private final Hoerbuchkatalog hoerbuchkatalog;

    private final DlsBestellung dlsBestellung;

    private final EmailTemplateBuilder emailTemplateBuilder;

    private final EmailService emailService;

    @Autowired
    public DownloadBestellungAufgegebenHandler(final Hoerbuchkatalog hoerbuchkatalog,
                                               final DlsBestellung dlsBestellung,
                                               final EmailTemplateBuilder emailTemplateBuilder,
                                               final EmailService emailService) {
        super(BestellungAufgegeben.class);
        logger.trace("Initializing");
        this.hoerbuchkatalog = hoerbuchkatalog;
        this.dlsBestellung = dlsBestellung;
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
        final List<Auftragsquittung> auftragsquittungen =
                dlsBestellung.pruefenUndBestellen(
                        domainEvent.getHoerernummer().getValue(),
                        Arrays.stream(aghNummern)
                                .map(AghNummer::getValue)
                                .toArray(String[]::new));
        final Set<Hoerbuch> hoerbuecher = Arrays.stream(aghNummern)
                .map(hoerbuchkatalog::hole)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        emailErzeugenArchivierenUndVersenden(domainEvent, bestellung, hoerbuecher);
    }

    private void emailErzeugenArchivierenUndVersenden(final BestellungAufgegeben domainEvent,
                                                      final Bestellung bestellung,
                                                      final Set<Hoerbuch> hoerbucher) {
        final String htmlEmail = emailTemplateBuilder.build(
                "BestellbestaetigungDownload.html",
                Map.of("bestellung", bestellung, "hoerbuecher", hoerbucher));
        emailArchivieren(domainEvent, htmlEmail);
        emailService.send(bestellung.getHoereremail().getValue(), "wbh@wbh-online.de",
                "Ihre Download-Bestellung bei der WBH",
                htmlEmail);
    }

    private void emailArchivieren(final BestellungAufgegeben domainEvent,
                                  final String htmlEmail) {
        try {
            final Path archivDatei =
                    Path.of("Archiv/Bestellungen", domainEvent.getDomainId() + "_email.html");
            Files.createDirectories(archivDatei.getParent());
            Files.write(archivDatei, htmlEmail.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(String.format(
                    "Kann E-Mail für Bestellung %snicht archivieren", domainEvent.getDomainId()),
                    e);
        }
    }

}
