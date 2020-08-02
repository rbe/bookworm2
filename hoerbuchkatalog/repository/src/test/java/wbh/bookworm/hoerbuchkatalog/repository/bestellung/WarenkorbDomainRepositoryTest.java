/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Warenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbId;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {BestellungTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class WarenkorbDomainRepositoryTest {

    private final WarenkorbRepository warenkorbRepository;

    @Autowired
    WarenkorbDomainRepositoryTest(final WarenkorbRepository warenkorbRepository) {
        this.warenkorbRepository = warenkorbRepository;
    }

    @Test
    void shouldPersistAndLoadCdWarenkorb() {
        final Hoerernummer hoerernummer = new Hoerernummer("12345");
        final WarenkorbId warenkorbId = new WarenkorbId("12345-CD");

        final Warenkorb cdWarenkorb1 = new CdWarenkorb(warenkorbId, hoerernummer);
        cdWarenkorb1.hinzufuegen(new Titelnummer("123"));
        warenkorbRepository.save(cdWarenkorb1);

        final Optional<CdWarenkorb> cdWarenkorb2 =
                warenkorbRepository.load(warenkorbId, CdWarenkorb.class);
        assertTrue(cdWarenkorb2.isPresent());
        assertEquals(warenkorbId, cdWarenkorb2.get().getDomainId());
        assertEquals(hoerernummer, cdWarenkorb2.get().getHoerernummer());
        assertTrue(cdWarenkorb2.get().enthalten(new Titelnummer("123")));
    }

    @Test
    void shouldPersistAndLoadDownloadWarenkorb() {
        final Hoerernummer hoerernummer = new Hoerernummer("12345");
        final WarenkorbId warenkorbId = new WarenkorbId("12345-Download");

        final Warenkorb downloadWarenkorb1 =
                new DownloadWarenkorb(warenkorbId, hoerernummer);
        downloadWarenkorb1.hinzufuegen(new Titelnummer("456"));
        warenkorbRepository.save(downloadWarenkorb1);

        final Optional<DownloadWarenkorb> downloadWarenkorb2 =
                warenkorbRepository.load(warenkorbId, DownloadWarenkorb.class);
        assertTrue(downloadWarenkorb2.isPresent());
        assertEquals(warenkorbId, downloadWarenkorb2.get().getDomainId());
        assertEquals(hoerernummer, downloadWarenkorb2.get().getHoerernummer());
        assertTrue(downloadWarenkorb2.get().enthalten(new Titelnummer("456")));
    }

}
