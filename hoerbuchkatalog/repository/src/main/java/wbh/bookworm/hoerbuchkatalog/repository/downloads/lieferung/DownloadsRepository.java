/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.downloads.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDownload;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferung;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import aoc.ddd.repository.DomainRespositoryComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@DomainRespositoryComponent
public class DownloadsRepository /* TODO implements DomainRepository<> */{

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsRepository.class);

    private final DlsLieferung dlsLieferung;

    private final Hoerbuchkatalog hoerbuchkatalog;

    @Autowired
    DownloadsRepository(final DlsLieferung dlsLieferung, final Hoerbuchkatalog hoerbuchkatalog) {
        this.dlsLieferung = dlsLieferung;
        this.hoerbuchkatalog = hoerbuchkatalog;
    }

    public HoererBlistaDownloads lieferungen(final Hoerernummer hoerernummer) {
        long startWerke = System.nanoTime();
        // TODO DownloadsArchiv
        final Optional<DlsLieferung.DlsWerke> werke = dlsLieferung.alleWerkeLaden(hoerernummer.getValue());
        if (werke.isPresent()&&!werke.get().hatFehler()) {
            final List<BlistaDownload> bereitgestellteDownloads = werke.get()
                    .books.parallelStream()
                    .map(book -> {
                        final AghNummer aghNummer = new AghNummer(book.Aghnummer);
                        final Optional<DlsLieferung.DlsBestellung> bestellung =
                                dlsLieferung.bestellungLaden(hoerernummer.getValue(), aghNummer.getValue());
                        if (bestellung.isPresent()) {
                            final DlsLieferung.DlsBestellung dlsBestellung = bestellung.get();
                            final Optional<Hoerbuch> hoerbuch = hoerbuchkatalog.hole(aghNummer);
                            return hoerbuch.map(h -> new BlistaDownload(
                                    hoerernummer,
                                    aghNummer,
                                    h.getTitelnummer(), h.getTitel(),
                                    h.getAutor(), h.getSpieldauer(),
                                    dlsBestellung.book.Ausleihstatus,
                                    dlsBestellung.book.Bestelldatum, dlsBestellung.book.Rueckgabedatum,
                                    dlsBestellung.book.DlsDescription,
                                    dlsBestellung.book.DownloadCount, dlsBestellung.book.MaxDownload,
                                    dlsBestellung.book.DownloadLink
                            )).orElse(null);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            LOGGER.trace("{}: Abholen aller Werke dauerte {} ms", Thread.currentThread().getName(),
                   (System.nanoTime() - startWerke) / 1_000_000);
            return new HoererBlistaDownloads(hoerernummer, bereitgestellteDownloads);
        } else {
            return null;
        }
    }

}
