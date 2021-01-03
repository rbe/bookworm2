/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.InputStream;
import java.util.Optional;

public interface HoerprobeService {

    Optional<InputStream> makeHoerprobeAsStream(String xMandant, String xHoerernummer,
                                                String titelnummer);

}
