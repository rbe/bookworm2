/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.util.List;

public interface KatalogService {

    AudiobookInfoDTO audiobookInfo(String hoerernummer, String titelnummer);

    PlaylistDTO playlist(String hoerernummer, String titelnummer);

    List<AudiobookInfoDTO> findAll(String hoerernummer, String[] keywords);

    boolean index();

}
