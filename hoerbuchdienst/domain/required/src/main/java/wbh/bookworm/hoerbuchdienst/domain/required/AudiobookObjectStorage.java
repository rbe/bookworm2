/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required;

import java.io.InputStream;

public interface AudiobookObjectStorage {

    InputStream track(String titelnummer, String ident);

    InputStream zipped(String titelnummer);

}
