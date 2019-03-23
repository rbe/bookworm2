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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class DownloadBestellungAufgegebenHandler extends DomainEventSubscriber<BestellungAufgegeben> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadBestellungAufgegebenHandler.class);

    private final Hoerbuchkatalog hoerbuchkatalog;

    private final DlsBestellung dlsBestellung;

    private final EmailTemplateBuilder emailTemplateBuilder;

    private final EmailService emailService;

    @Autowired
    DownloadBestellungAufgegebenHandler(final Hoerbuchkatalog hoerbuchkatalog,
                                        final DlsBestellung dlsBestellung,
                                        final EmailTemplateBuilder emailTemplateBuilder,
                                        final EmailService emailService) {
        super(BestellungAufgegeben.class);
        logger.trace("Initializing {}", this);
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
        final AghNummer[] aghNummern = hoerbuchkatalog.titelnummerZuAghNummer(downloadTitelnummern);
        if (aghNummern.length > 0) {
            logger.info("Hörer {} hat folgende Downloads bestellt: {}/{}",
                    hoerernummer, downloadTitelnummern, aghNummern);
            // TODO Auftragsquittungen auswerten und E-Mail anpassen (erfolglose Bestellungen)?
            // TODO Alternativ Bestellung wiederholen
            final List<Auftragsquittung> auftragsquittungen = auftraegePruefen(domainEvent, aghNummern);
            final Set<Hoerbuch> hoerbuecher = Arrays.stream(aghNummern)
                    .map(hoerbuchkatalog::hole)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
            csvDateiErgaenzen(hoerernummer, hoerbuecher);
            emailErzeugenArchivierenUndVersenden(domainEvent, bestellung, hoerbuecher, auftragsquittungen);
        } else {
            LOGGER.info("Hörer {} hat am {} keine Downloads bestellt",
                    hoerernummer, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    private List<Auftragsquittung> auftraegePruefen(final BestellungAufgegeben domainEvent,
                                                    final AghNummer[] aghNummern) {
        return dlsBestellung.pruefenUndBestellen(domainEvent.getHoerernummer().getValue(),
                Arrays.stream(aghNummern)
                        .map(AghNummer::getValue)
                        .toArray(String[]::new));
    }

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HHmmss");

    @SuppressWarnings({"squid:S3457"})
    private void csvDateiErgaenzen(final Hoerernummer hoerernummer, final Set<Hoerbuch> hoerbuecher) {
        final LocalDateTime now = LocalDateTime.now();
        final List<String> strings = hoerbuecher.stream().map(hoerbuch -> {
            final LocalDateTime rueckgabedatum = now.plusMonths(1);
            return String.format("%5s %6s %13s %11s %8s %6s %1s %8s %6s %8s %6s\r\n",
                    /* HOENR */hoerernummer,
                    /* TITNR */hoerbuch.getTitelnummer(), /* TIAGNR */hoerbuch.getAghNummer(),
                    /* DLSID */"unbekannt01",
                    /* ABFRDT */now.format(YYYY_MM_DD), /* ABFRZT */now.format(HH_MM_SS),
                    /* STATUS */"0",
                    /* AUSLDT, Ausleihdatum */now.format(YYYY_MM_DD), /* AUSLZT, Ausleihdatum */now.format(HH_MM_SS),
                    /* RUEGDT */rueckgabedatum.format(YYYY_MM_DD), /* RUEGZT */rueckgabedatum.format(HH_MM_SS));
        }).collect(Collectors.toUnmodifiableList());
        // TODO Synchronize, per Queue?
        final Path path = Path.of("webhoer-", now.format(YYYY_MM_DD));
        try {
            Files.write(path, strings, StandardCharsets.ISO_8859_1,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.warn("{} kann nicht beschrieben werden: {}", path, strings);
            LOGGER.error("" + path, e);
        }
    }

    private void emailErzeugenArchivierenUndVersenden(final BestellungAufgegeben domainEvent,
                                                      final Bestellung bestellung,
                                                      final Set<Hoerbuch> hoerbucher,
                                                      final List<Auftragsquittung> auftragsquittungen) {
        final String htmlEmail = emailTemplateBuilder.build(
                "BestellbestaetigungDownload.html",
                Map.of("bestellung", bestellung,
                        "hoerbuecher", hoerbucher));
        emailArchivieren(domainEvent, htmlEmail);
        emailService.send(bestellung.getHoereremail().getValue(), /* TODO Konfiguration */"wbh@wbh-online.de",
                /* TODO Konfiguration */"Ihre Download-Bestellung bei der WBH",
                htmlEmail);
    }

    private void emailArchivieren(final BestellungAufgegeben domainEvent,
                                  final String htmlEmail) {
        try {
            final Path archivDatei =
                    Path.of(/* TODO Konfiguration */"var/repository/Bestellung",
                            domainEvent.getDomainId() + "-DownloadBestellung.html");
            Files.createDirectories(archivDatei.getParent());
            Files.write(archivDatei, htmlEmail.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(String.format(
                    "Kann E-Mail für Bestellung %s nicht archivieren", domainEvent.getDomainId()),
                    e);
        }
    }

}
