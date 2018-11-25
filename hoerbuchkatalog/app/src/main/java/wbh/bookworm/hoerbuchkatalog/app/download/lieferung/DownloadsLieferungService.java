/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.download.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;
import wbh.bookworm.hoerbuchkatalog.repository.downloads.lieferung.DownloadsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadsLieferungService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsLieferungService.class);

    private final DownloadsRepository downloadsRepository;

    @Autowired
    public DownloadsLieferungService(final DownloadsRepository downloadsRepository) {
        this.downloadsRepository = downloadsRepository;
    }

    public HoererBlistaDownloads lieferungen(final Hoerernummer hoerernummer) {
        return downloadsRepository.lieferungen(hoerernummer);
    }

}
