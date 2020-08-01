/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports.audiobook;

import java.io.InputStream;

public interface AudiobookService {

    String shardLocation(/* TODO AghNummer */String titelnummer);

    boolean locatedLocal(/* TODO AghNummer */String titelnummer);

    InputStream trackAsStream(String hoerernummer, /* TODO AghNummer */String titelnummer, String ident);

    InputStream zipAsStream(String hoerernummer, /* TODO AghNummer */String titelnummer);

    void orderZip(String hoerernummer, String titelnummer, String orderId);

    String orderStatus(String orderId);

    InputStream fetchOrder(String orderId);

    boolean putZip(String titelnummer, /* TODO AghNummer */InputStream inputStream, String hash);

}
