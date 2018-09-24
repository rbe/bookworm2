/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.email.TemplateBuilder;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.MerklisteFactory;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbFactory;
import wbh.bookworm.hoerbuchkatalog.domain.email.EmailTemplate;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.MerklisteRepository;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.WarenkorbRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BestellungService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellungService.class);

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final MerklisteFactory merklisteFactory;

    private final MerklisteRepository merklisteRepository;

    private final WarenkorbFactory warenkorbFactory;

    private final WarenkorbRepository warenkorbRepository;

    private final TemplateBuilder templateBuilder;

    private final EmailService emailService;

    @Autowired
    public BestellungService(final HoerbuchkatalogService hoerbuchkatalogService,
                             final MerklisteFactory merklisteFactory,
                             final MerklisteRepository merklisteRepository,
                             final WarenkorbFactory warenkorbFactory,
                             final WarenkorbRepository warenkorbRepository,
                             final TemplateBuilder templateBuilder,
                             final EmailService emailService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.merklisteFactory = merklisteFactory;
        this.merklisteRepository = merklisteRepository;
        this.warenkorbFactory = warenkorbFactory;
        this.warenkorbRepository = warenkorbRepository;
        this.templateBuilder = templateBuilder;
        this.emailService = emailService;
    }

    private Merkliste merkliste(final Hoerernummer hoerernummer) {
        return merklisteRepository.load(hoerernummer)
                .orElseGet(() -> merklisteFactory.erstellen(hoerernummer));
    }

    public int anzahlAufMerkliste(final Hoerernummer hoerernummer) {
        return merkliste(hoerernummer).getAnzahl();
    }

    public Set<Titelnummer> titelnummernAufMerkliste(final Hoerernummer hoerernummer) {
        return merkliste(hoerernummer).getTitelnummern();
    }

    public void aufDieMerklisteSetzen(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        merkliste(hoerernummer).hinzufuegen(titelnummer);
    }

    public void vonMerklisteEntfernen(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        merkliste(hoerernummer).entfernen(titelnummer);
    }

    private CdWarenkorb cdWarenkorb(final Hoerernummer hoerernummer) {
        return (CdWarenkorb) warenkorbRepository.load(hoerernummer)
                .orElseGet(() -> warenkorbFactory.cdErstellen(hoerernummer));
    }

    public void inDenCdWarenkorb(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        cdWarenkorb(hoerernummer).hinzufuegen(titelnummer);
    }

    public void ausDeMCdWarenkorb(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        cdWarenkorb(hoerernummer).hinzufuegen(titelnummer);
    }

    public void cdBestellen(final Hoerernummer hoerernummer,
                            final CdWarenkorb/*TODO Id*/ cdWarenkorb) {
        templateBuilder.build(new EmailTemplate(), "");
    }

    private DownloadWarenkorb downloadWarenkorb(final Hoerernummer hoerernummer) {
        return (DownloadWarenkorb) warenkorbRepository.load(hoerernummer)
                .orElseGet(() -> warenkorbFactory.downloadErstellen(hoerernummer));
    }

    public void downloadBestellen(final Hoerernummer hoerernummer,
                                  final DownloadWarenkorb/*TODO Id*/ downloadWarenkorb) {
        // TODO hoerbuecherAufMerklisteVerschieben(getHoerernummer(), aghNummern);
    }

}
