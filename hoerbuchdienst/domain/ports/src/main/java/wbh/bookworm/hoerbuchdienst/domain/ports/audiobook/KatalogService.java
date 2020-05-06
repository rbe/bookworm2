/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports.audiobook;

import java.util.List;

public interface KatalogService {

    AudiobookInfoDTO audiobookInfo(String hoerernummer, String titelnummer);

    TrackInfoDTO trackInfo(String hoerernummer, String titelnummer, String ident);

    PlaylistDTO playlist(String hoerernummer, String titelnummer);

    boolean index();

    List<AudiobookInfoDTO> findAll(String hoerernummer, String[] keywords);

}
