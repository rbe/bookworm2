/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;
import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

@SpringBootTest(classes = {LieferungAppConfig.class})
@ExtendWith(SpringExtension.class)
class DownloadsRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsRepositoryTest.class);

    private final DownloadsRepository downloadsRepository;

    @Autowired
    DownloadsRepositoryTest(final DownloadsRepository downloadsRepository) {
        this.downloadsRepository = downloadsRepository;
    }

    @Test
    void shouldAlleLieferungenAbrufen() {
        final HoererBlistaDownloads lieferungen = downloadsRepository
                .lieferungen(new Hoerernummer("80170"));
        LOGGER.debug("{}", lieferungen);
    }

}
