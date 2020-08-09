/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports.audiobook;

import java.io.InputStream;

public interface AudiobookLocationService {

    String shardLocation(/* TODO AghNummer */String titelnummer);

    boolean isLocatedLocal(/* TODO AghNummer */String titelnummer);

    boolean receiveObject(String objectId, InputStream inputStream, String hashValue);

}
