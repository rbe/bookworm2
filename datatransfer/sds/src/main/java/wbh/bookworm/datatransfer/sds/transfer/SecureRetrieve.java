/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.sds.transfer;

import aoc.mikrokosmos.incubation.transaction.Base64DataContainer;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("retrieve")
public class SecureRetrieve {

    public Base64DataContainer getDecrypted() {
        return null;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Base64DataContainer get(@RequestBody RetrieveTxMetadata retrieveTxMetadata) {
        return null;
    }

}
