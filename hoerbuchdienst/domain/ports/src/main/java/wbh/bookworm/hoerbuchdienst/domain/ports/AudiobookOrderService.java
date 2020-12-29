/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public interface AudiobookOrderService {

    void orderZip(String mandant, String hoerernummer, String titelnummer, String orderId);

    String orderStatus(String orderId);

    Optional<InputStream> fetchOrderAsStream(String orderId);

    Optional<Path> fetchOrderAsFile(String orderId);

    Optional<Long> fileSize(String orderId);

    Optional<Long> lastModified(String orderId);

}
