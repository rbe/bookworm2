/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.crypto.pgp;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

final class PgpManager {

    static {
        if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private static ArmoredKey base64Encode(PGPSecretKey secretKey) {
        final ByteArrayOutputStream secretKeyOutputStream = new ByteArrayOutputStream();
        final ArmoredOutputStream armoredSecretKeyOutputStream = new ArmoredOutputStream(secretKeyOutputStream);
        try {
            secretKey.encode(armoredSecretKeyOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ArmoredKey(secretKeyOutputStream.toString());
    }

    private static ArmoredKey base64Encode(PGPPublicKey publicKey) {
        final ByteArrayOutputStream secretKeyOutputStream = new ByteArrayOutputStream();
        final ArmoredOutputStream armoredSecretKeyOutputStream = new ArmoredOutputStream(secretKeyOutputStream);
        try {
            publicKey.encode(armoredSecretKeyOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ArmoredKey(secretKeyOutputStream.toString());
    }

    private static PGPSecretKey createKey(final String identity, final char[] passphrase) {
        final KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        kpg.initialize(2048);
        final KeyPair keyPair = kpg.generateKeyPair();
        final PGPKeyPair pgpKeyPair;
        try {
            pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, keyPair, new Date());
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
        final JcaPGPContentSignerBuilder certificationSignerBuilder =
                new JcaPGPContentSignerBuilder(pgpKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA512);
        final PGPDigestCalculator sha1Calculator;
        try {
            sha1Calculator = new JcaPGPDigestCalculatorProviderBuilder()
                    .build().get(HashAlgorithmTags.SHA1);
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
        final JcePBESecretKeyEncryptorBuilder pbeSecretKeyEncryptorBuilder =
                new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calculator);
        try {
            return new PGPSecretKey(
                    PGPSignature.DEFAULT_CERTIFICATION,
                    pgpKeyPair, identity, sha1Calculator,
                    null, null,
                    certificationSignerBuilder,
                    pbeSecretKeyEncryptorBuilder.setProvider("BC").build(passphrase));
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
    }

    public ArmoredKeyPair createArmoredKeyPair(final String identity, final char[] passphrase) {
        final PGPSecretKey secretKey = createKey(identity, passphrase);
        return ArmoredKeyPair.of(base64Encode(secretKey), base64Encode(secretKey.getPublicKey()));
    }

    public static void main(String[] args) throws Exception {
        final PgpManager pgpManager = new PgpManager();
        long start1 = System.nanoTime();
        final char[] passphrase = UUID.randomUUID().toString().substring(1, 32).toCharArray();
        final ArmoredKeyPair armoredKeyPair = pgpManager.createArmoredKeyPair("Ralf", passphrase);
        System.out.println("Key generation took " +
                Duration.ofNanos(System.nanoTime() - start1).toMillis() + " ms");
    }

}
