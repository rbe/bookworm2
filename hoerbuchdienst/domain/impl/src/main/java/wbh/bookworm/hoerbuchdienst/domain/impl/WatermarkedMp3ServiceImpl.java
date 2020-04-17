/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import wbh.bookworm.hoerbuchdienst.domain.ports.WatermarkedMp3Service;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookRepository;

@Singleton
final class WatermarkedMp3ServiceImpl implements WatermarkedMp3Service {

    private final AudiobookRepository audiobookRepository;

    @Inject
    WatermarkedMp3ServiceImpl(final AudiobookRepository audiobookRepository) {
        this.audiobookRepository = audiobookRepository;
    }

    @Override
    public byte[] track(final String titelnummer, final String ident) {
        final byte[] mp3 = audiobookRepository.track(titelnummer, ident);
        // TODO Insert watermark
        return mp3;
    }

}
