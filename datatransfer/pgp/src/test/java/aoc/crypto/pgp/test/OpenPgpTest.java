/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.crypto.pgp.test;

import aoc.crypto.pgp.ArmoredKey;
import aoc.crypto.pgp.ArmoredKeyPair;
import aoc.crypto.pgp.OpenPgp;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenPgpTest {

    @Test
    void shouldEncryptAsBobAndDecryptAsAlice() {
        final String message = "Hello World!";

        final OpenPgp openPGP = new OpenPgp();

        final String senderUserIdName = "Bob Tester";
        final String senderUserIdEmail = "bob@example.com";
        final String senderUserId = String.format("%s <%s>", senderUserIdName, senderUserIdEmail);
        final String senderPassphrase = UUID.randomUUID().toString();
        final ArmoredKeyPair senderArmoredKeyPair = openPGP.generateArmoredKeyPair(2048,
                senderUserIdName, senderUserIdEmail,
                senderPassphrase);

        final String receiverUserIdName = "Alice Tester";
        final String receiverUserIdEmail = "alice@example.com";
        final String receiverUserId = String.format("%s <%s>", receiverUserIdName, receiverUserIdEmail);
        final String receiverPassphrase = UUID.randomUUID().toString();
        final ArmoredKeyPair receiverArmoredKeyPair = openPGP.generateArmoredKeyPair(2048,
                receiverUserIdName, receiverUserIdEmail,
                receiverPassphrase);

        final String encryptedMessage = openPGP.encryptAndSign(message,
                senderUserIdEmail, senderPassphrase, senderArmoredKeyPair,
                receiverUserId, receiverArmoredKeyPair.publicKey());

        final ArmoredKey senderPublicKey = senderArmoredKeyPair.publicKey();
        final String decryptedMessage = openPGP.decryptAndVerify(encryptedMessage,
                receiverPassphrase, receiverArmoredKeyPair,
                senderUserIdEmail, senderPublicKey);

        assertEquals(message, decryptedMessage);
    }

}
