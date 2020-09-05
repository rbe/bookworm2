/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDownload;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferung;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsBook;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsWerke;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;
import wbh.bookworm.shared.domain.AghNummer;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.DomainRepositoryComponent;

@DomainRepositoryComponent
public class DownloadsRepository /* TODO implements DomainRepository<> */ {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsRepository.class);

    private final RepositoryResolver repositoryResolver;

    private final DlsLieferung dlsLieferung;

    @Autowired
    DownloadsRepository(final RepositoryResolver repositoryResolver,
                        final DlsLieferung dlsLieferung) {
        this.repositoryResolver = repositoryResolver;
        this.dlsLieferung = dlsLieferung;
    }

    public long anzahlLieferungenHeute(final Hoerernummer hoerernummer) {
        long anzahl = 0L;
        final Optional<DlsWerke> maybeDlsWerke = dlsLieferung.alleWerkeLaden(hoerernummer.getValue());
        if (maybeDlsWerke.isPresent()) {
            final DlsWerke dlsWerke = maybeDlsWerke.get();
            if (!dlsWerke.hatFehler()) {
                anzahl = dlsWerke.books
                        .stream()
                        .map(b -> toBlistaDownload(hoerernummer, b))
                        .filter(Objects::nonNull)
                        .filter(b -> b.getAusleihstatus() > 0 && b.getAusleihstatus() < 4
                                && b.getBestelldatum().getDayOfYear() == LocalDateTime.now().getDayOfYear())
                        .count();
                LOGGER.info("Hörer {} hat am {} bereits {} Download(s) beauftragt",
                        hoerernummer,
                        LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.GERMANY)),
                        anzahl);
            }
        }
        return anzahl;
    }

    public long anzahlLieferungenInDiesemMonat(final Hoerernummer hoerernummer) {
        long anzahl = 0L;
        final Optional<DlsWerke> maybeDlsWerke = dlsLieferung.alleWerkeLaden(hoerernummer.getValue());
        if (maybeDlsWerke.isPresent()) {
            final DlsWerke dlsWerke = maybeDlsWerke.get();
            if (!dlsWerke.hatFehler()) {
                anzahl = dlsWerke.books
                        .stream()
                        .map(b -> toBlistaDownload(hoerernummer, b))
                        .filter(Objects::nonNull)
                        .filter(b -> b.getAusleihstatus() > 0 && b.getAusleihstatus() < 4
                                && b.getRueckgabedatum().getMonth().equals(LocalDateTime.now().getMonth()))
                        .count();
                LOGGER.info("Hörer {} hat im Monat {} {} Downloads beauftragt",
                        hoerernummer, LocalDateTime.now().getMonth(), anzahl);
            }
        }
        return anzahl;
    }

    public HoererBlistaDownloads lieferungen(final Hoerernummer hoerernummer) {
        LOGGER.trace("Hole blista Werke für Hörer {}", hoerernummer);
        final long startWerke = System.nanoTime();
        final Optional<DlsWerke> maybeAlleWerke = dlsLieferung.alleWerkeLaden(hoerernummer.getValue());
        if (maybeAlleWerke.isPresent()) {
            final DlsWerke alleWerke = maybeAlleWerke.get();
            if (!alleWerke.hatFehler()) {
                final List<BlistaDownload> bereitgestellteDownloads = alleWerke
                        .books.parallelStream()
                        .filter(book -> book.Ausleihstatus.equals("1")
                                || book.Ausleihstatus.equals("2")
                                || book.Ausleihstatus.equals("3"))
                        .map(book -> toBlistaDownload(hoerernummer, book))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                LOGGER.debug("{}: Abholen von {} blista Werken für Hörer {} dauerte {} ms",
                        Thread.currentThread().getName(),
                        bereitgestellteDownloads.size(),
                        hoerernummer,
                        (System.nanoTime() - startWerke) / 1_000_000);
                return new HoererBlistaDownloads(hoerernummer, bereitgestellteDownloads);
            } else {
                return new HoererBlistaDownloads(hoerernummer,
                        alleWerke.getFehlercode(), alleWerke.getFehlermeldung());
            }
        } else {
            return new HoererBlistaDownloads(hoerernummer,
                    "42",
                    "Die Downloads konnten leider nicht bei der blista abgerufen werden.");
        }
    }

    private BlistaDownload toBlistaDownload(final Hoerernummer hoerernummer,
                                            final DlsWerke.Book book) {
        final AghNummer aghNummer = new AghNummer(book.Aghnummer);
        final Optional<DlsBook> bestellung =
                dlsLieferung.bestellungLaden(hoerernummer.getValue(), aghNummer.getValue());
        if (bestellung.isPresent()) {
            final DlsBook dlsBook = bestellung.get();
            final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
            final Optional<Hoerbuch> hoerbuch = hoerbuchkatalog.hole(aghNummer);
            if (hoerbuch.isEmpty()) {
                LOGGER.warn("Hörer {}/AGH Nummer {}: Hörbuch nicht gefunden",
                        hoerernummer, aghNummer);
                final BlistaDownload blistaDownload = BlistaDownload.of(hoerernummer,
                        Hoerbuch.unbekannterDownload(aghNummer, book.Title),
                        dlsBook.book.Ausleihstatus,
                        dlsBook.book.Bestelldatum,
                        dlsBook.book.Rueckgabedatum,
                        dlsBook.book.DlsDescription,
                        dlsBook.book.DownloadCount,
                        dlsBook.book.MaxDownload,
                        dlsBook.book.DownloadLink,
                        dlsBook.book.Gesperrt);
                LOGGER.debug("{}", blistaDownload);
                return blistaDownload;
            } else {
                final BlistaDownload blistaDownload = BlistaDownload.of(hoerernummer,
                        hoerbuch.orElseThrow(IllegalStateException::new),
                        dlsBook.book.Ausleihstatus,
                        dlsBook.book.Bestelldatum,
                        dlsBook.book.Rueckgabedatum,
                        dlsBook.book.DlsDescription,
                        dlsBook.book.DownloadCount,
                        dlsBook.book.MaxDownload,
                        dlsBook.book.DownloadLink,
                        dlsBook.book.Gesperrt);
                LOGGER.debug("{}", blistaDownload);
                return blistaDownload;
            }
        } else {
            LOGGER.warn("Hörer {}/AGH Nummer {}: Bestellung bei der blista nicht gefunden",
                    hoerernummer, aghNummer);
        }
        return null;
    }

}
