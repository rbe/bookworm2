/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.sds.transfer;

import aoc.mikrokosmos.crypto.pgp.ArmoredKeyPair;
import aoc.mikrokosmos.crypto.pgp.OpenPgp;
import aoc.mikrokosmos.incubation.transaction.Base64DataContainer;
import aoc.mikrokosmos.incubation.transaction.TxId;
import aoc.mikrokosmos.incubation.transaction.TxResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("store")
public class SecureStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureStore.class);

    private static final int PGP_KEY_SIZE = 2048;

    private final OpenPgp openPgp;

    private final String userIdName;

    private final String userIdEmail;

    private final String userId;

    private final Map<StoreTxMetadata, ArmoredKeyPair> txMetaDataArmoredKeyPairs;

    public SecureStore() {
        openPgp = new OpenPgp();
        userIdName = "Secure Data Storage";
        userIdEmail = "sds@example.com";
        userId = String.format("%s <%s>", userIdName, userIdEmail);
        txMetaDataArmoredKeyPairs = new HashMap<>();
    }

    private ArmoredKeyPair armoredKeyPair() {
        final String passphrase = UUID.randomUUID().toString();
        return openPgp.generateArmoredKeyPair(PGP_KEY_SIZE, userIdName, userIdEmail, passphrase);
    }

    @Scheduled(fixedRate = 10_000)
    private void checkTransactions() {
        LOGGER.debug("Cleaning up transactions ids");
        for (StoreTxMetadata storeTxMetaData : txMetaDataArmoredKeyPairs.keySet()) {
            // created + 300 > now
            if (storeTxMetaData.getCreated().plusSeconds(300).isAfter(LocalDateTime.now())) {
                LOGGER.info("Closing transaction {} due to timeout", storeTxMetaData.getTxId());
            }
        }
    }

    @GetMapping(value = "/begin",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public StoreTxMetadata begin() {
        final ArmoredKeyPair armoredKeyPair = armoredKeyPair();
        final StoreTxMetadata storeTxMetaData = new StoreTxMetadata(armoredKeyPair.publicKey());
        txMetaDataArmoredKeyPairs.put(storeTxMetaData, armoredKeyPair);
        // TODO mkdir txid
        // TODO store private key in Vault
        LOGGER.info("Created transaction {}", storeTxMetaData);
        return storeTxMetaData;
    }

    @PostMapping(value = "/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean add(@RequestBody final Base64DataContainer base64DataContainer) {
        final TxId txId = base64DataContainer.getTxId();
        if (txMetaDataArmoredKeyPairs.containsKey(txId)) {
            final ArmoredKeyPair armoredKeyPair = txMetaDataArmoredKeyPairs.get(txId);
            System.out.println(armoredKeyPair);
            System.out.println(base64DataContainer);
            // TODO write data to disk
            return true;
        } else {
            LOGGER.warn("Unknown or closed transaction id {}", txId);
            return false;
        }
    }

    @PostMapping(value = "/commit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TxResult commit(@RequestBody final TxId txId) {
        if (txMetaDataArmoredKeyPairs.containsKey(txId)) {
            txMetaDataArmoredKeyPairs.remove(txId);
            LOGGER.info("Comitting transaction {}", txId);
            return TxResult.success();
        } else {
            LOGGER.warn("Cannot commit unknown or already closed transaction {}", txId);
            return TxResult.unknownTxId(txId);
        }
    }

}
