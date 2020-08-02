/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailTemplateBuilder;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung.Auftragsquittung;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung.DlsBestellung;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;
import wbh.bookworm.shared.domain.hoerbuch.AghNummer;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;
import aoc.mikrokosmos.ddd.event.DomainEventSubscriber;

@Component
class DownloadBestellungAufgegebenHandler extends DomainEventSubscriber<BestellungAufgegeben> {

    private final Object monitor = new Object();

    private final RepositoryResolver repositoryResolver;

    private final DlsBestellung dlsBestellung;

    private final EmailTemplateBuilder emailTemplateBuilder;

    private final EmailService emailService;

    @Autowired
    DownloadBestellungAufgegebenHandler(final RepositoryResolver repositoryResolver,
                                        final DlsBestellung dlsBestellung,
                                        final EmailTemplateBuilder emailTemplateBuilder,
                                        final EmailService emailService) {
        super(BestellungAufgegeben.class);
        logger.trace("Initializing {}", this);
        this.repositoryResolver = repositoryResolver;
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
        final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
        final AghNummer[] aghNummern = hoerbuchkatalog.titelnummerZuAghNummer(downloadTitelnummern);
        if (aghNummern.length > 0) {
            logger.info("Hörer {} hat folgende Downloads bestellt: {}/{}",
                    hoerernummer, downloadTitelnummern, aghNummern);
            // TODO auftragsquittungen auswerten und E-Mail anpassen (erfolglose Bestellungen)? Alternativ Bestellung wiederholen
            final List<Auftragsquittung> auftragsquittungen = auftraegePruefen(domainEvent, aghNummern);
            if (auftragsquittungen.stream().allMatch(Auftragsquittung::isUebermittlungOk)) {
                final Set<Hoerbuch> hoerbuecher = Arrays.stream(aghNummern)
                        .map(hoerbuchkatalog::hole)
                        .flatMap(Optional::stream)
                        .collect(Collectors.toSet());
                // TODO CSV-Datei nur bei erfolgreicher Bestellung ergänzen
                csvDateiErgaenzen(hoerernummer, hoerbuecher);
                // TODO E-Mail nur bei erfolgreicher Bestellung versenden
                emailErzeugenArchivierenUndVersenden(domainEvent, bestellung, hoerbuecher/*, auftragsquittungen*/);
            } else {
                logger.error("{} konnte nicht aufgegeben werden", bestellung);
            }
        } else if (logger.isInfoEnabled()) {
            logger.info("Hörer {} hat am {} keine Downloads bestellt",
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

    @SuppressWarnings({"squid:S3457", "java:S3457"})
    private void csvDateiErgaenzen(final Hoerernummer hoerernummer, final Set<Hoerbuch> hoerbuecher) {
        final LocalDateTime now = LocalDateTime.now();
        final List<String> strings = hoerbuecher.stream().map(hoerbuch -> {
            final LocalDateTime rueckgabedatum = now.plusMonths(1L);
            return String.format("%5s %6s %13s %11s %8s %6s %1s %8s %6s %8s %6s\r\n",
                    /* HOENR */hoerernummer,
                    /* TITNR */hoerbuch.getTitelnummer(), /* TIAGNR */hoerbuch.getAghNummer(),
                    /* DLSID */"unbekannt01",
                    /* ABFRDT */now.format(YYYY_MM_DD), /* ABFRZT */now.format(HH_MM_SS),
                    /* STATUS */"0",
                    /* AUSLDT, Ausleihdatum */now.format(YYYY_MM_DD), /* AUSLZT, Ausleihdatum */now.format(HH_MM_SS),
                    /* RUEGDT */rueckgabedatum.format(YYYY_MM_DD), /* RUEGZT */rueckgabedatum.format(HH_MM_SS));
        }).collect(Collectors.toUnmodifiableList());
        // TODO Konfiguration
        final Path path = Path.of(String.format("var/wbh/aktualisierung/ausgangskorb/webhoer-%s.csv", now.format(YYYY_MM_DD)));
        // TODO Synchronize, per Queue?
        synchronized (monitor) {
            try {
                Files.write(path, strings, StandardCharsets.ISO_8859_1,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.warn("{} kann nicht beschrieben werden: {}", path, strings);
                logger.error("{}", path, e);
            }
        }
    }

    private void emailErzeugenArchivierenUndVersenden(final BestellungAufgegeben domainEvent,
                                                      final Bestellung bestellung,
                                                      final Set<Hoerbuch> hoerbucher/*,
            TODO Auftragsquittungen in E-Mail berücksichtigen final List<Auftragsquittung> auftragsquittungen*/) {
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
