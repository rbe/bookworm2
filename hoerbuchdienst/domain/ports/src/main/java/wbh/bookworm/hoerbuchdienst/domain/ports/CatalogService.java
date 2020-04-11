/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.util.List;

public interface CatalogService {

    AudiobookInfoDTO audiobookInfo(String titelnummer);

    PlaylistDTO playlist(String titelnummer);

    List<AudiobookInfoDTO> findAll(String keyword);

    boolean index();

}
