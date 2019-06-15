/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.crypto.pgp;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.callbacks.KeyringConfigCallbacks;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.bouncycastle.util.io.Streams;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class OpenPgp {

    private static final int CERTAINTY = 12;

    private static final BigInteger PUBLIC_EXPONENT = BigInteger.valueOf(0x10001);

    private static final int S2K_COUNT = 0xc0;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private final SecureRandom secureRandom;

    public OpenPgp() {
        try {
            this.secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ArmoredKeyPair generateArmoredKeyPair(int keySize,
                                                 String userIdName, String userIdEmail,
                                                 String passphrase) {

        Date now = new Date();
        RSAKeyPairGenerator keyPairGenerator = keyPairGenerator(keySize);
        PGPKeyPair encryptionKeyPair = encryptionKeyPair(now, keyPairGenerator);
        PGPSignatureSubpacketVector encryptionKeySignature = encryptionKeySignature();
        PGPKeyPair signingKeyPair = signingKeyPair(keyPairGenerator, now);
        PGPSignatureSubpacketVector signingKeySignature = signingKeySignature();
        PGPKeyRingGenerator keyRingGenerator = null;
        try {
            final BcPGPContentSignerBuilder keySignerBuilder = new BcPGPContentSignerBuilder(
                    signingKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1);
            keyRingGenerator = new PGPKeyRingGenerator(
                    PGPSignature.POSITIVE_CERTIFICATION,
                    signingKeyPair,
                    String.format("%s <%s>", userIdName, userIdEmail),
                    new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1),
                    signingKeySignature,
                    null,
                    keySignerBuilder,
                    secretKeyEncryptor(passphrase)
            );
        } catch (PGPException e) {
            e.printStackTrace();
        }
        if (null == keyRingGenerator) {
            throw new IllegalStateException();
        }
        try {
            keyRingGenerator.addSubKey(encryptionKeyPair, encryptionKeySignature, null);
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
        return ArmoredKeyPair.of(
                generateArmoredSecretKeyRing(keyRingGenerator),
                generateArmoredPublicKeyRing(keyRingGenerator));
    }

    private RSAKeyPairGenerator keyPairGenerator(int keySize) {
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
        keyPairGenerator.init(new RSAKeyGenerationParameters(PUBLIC_EXPONENT, secureRandom, keySize, CERTAINTY));
        return keyPairGenerator;
    }

    private PGPKeyPair encryptionKeyPair(Date now, RSAKeyPairGenerator rsaKeyPairGenerator) {
        try {
            return new BcPGPKeyPair(PGPPublicKey.RSA_GENERAL, rsaKeyPairGenerator.generateKeyPair(), now);
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
    }

    private PGPKeyPair signingKeyPair(RSAKeyPairGenerator rsaKeyPairGenerator, Date date) {
        try {
            return new BcPGPKeyPair(PGPPublicKey.RSA_SIGN, rsaKeyPairGenerator.generateKeyPair(), date);
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
    }

    private PGPSignatureSubpacketVector encryptionKeySignature() {
        PGPSignatureSubpacketGenerator encryptionKeySignatureGenerator = new PGPSignatureSubpacketGenerator();
        encryptionKeySignatureGenerator.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
        return encryptionKeySignatureGenerator.generate();
    }

    private PGPSignatureSubpacketVector signingKeySignature() {
        PGPSignatureSubpacketGenerator signingKeySignatureGenerator = new PGPSignatureSubpacketGenerator();
        signingKeySignatureGenerator.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER); // GPG seems to generate keys with ENCRYPT_COMMS and ENCRYPT_STORAGE flags. However this is the signing key, so I'd avoid setting those flags. Omitting them does not seem to have an impact on the functioning of BouncyGPG...
        signingKeySignatureGenerator.setPreferredSymmetricAlgorithms(false, new int[]{SymmetricKeyAlgorithmTags.AES_256});
        signingKeySignatureGenerator.setPreferredHashAlgorithms(false, new int[]{HashAlgorithmTags.SHA512});
        signingKeySignatureGenerator.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);
        return signingKeySignatureGenerator.generate();
    }

    private PBESecretKeyEncryptor secretKeyEncryptor(String passphrase) {
        try {
            return new BcPBESecretKeyEncryptorBuilder(
                    PGPEncryptedData.AES_256,
                    new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA256),
                    S2K_COUNT)
                    .build(passphrase.toCharArray());
        } catch (PGPException e) {
            throw new RuntimeException(e);
        }
    }

    private ArmoredKey generateArmoredSecretKeyRing(PGPKeyRingGenerator keyRingGenerator) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(armoredOutputStream)) {
            keyRingGenerator.generateSecretKeyRing().encode(bufferedOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return new ArmoredKey(outputStream.toString(UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private ArmoredKey generateArmoredPublicKeyRing(PGPKeyRingGenerator keyRingGenerator) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(armoredOutputStream)) {
            keyRingGenerator.generatePublicKeyRing().encode(bufferedOutputStream, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return new ArmoredKey(outputStream.toString(UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptAndSign(String unencryptedMessage,
                                 String senderUserIdEmail,
                                 String senderPassphrase,
                                 ArmoredKeyPair senderArmoredKeyPair,
                                 String receiverUserId,
                                 ArmoredKey receiverArmoredPublicKey) {
        InMemoryKeyring keyring = null;
        keyring = keyring(senderPassphrase, senderArmoredKeyPair, receiverArmoredPublicKey);
        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(encryptedOutputStream);
             OutputStream bouncyGPGOutputStream = BouncyGPG.encryptToStream()
                     .withConfig(keyring)
                     .withStrongAlgorithms()
                     .toRecipient(receiverUserId)
                     .andSignWith(senderUserIdEmail)
                     .armorAsciiOutput()
                     .andWriteTo(bufferedOutputStream)) {
            Streams.pipeAll(new ByteArrayInputStream(unencryptedMessage.getBytes()), bouncyGPGOutputStream);
        } catch (IOException | NoSuchProviderException | PGPException | SignatureException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            return encryptedOutputStream.toString(UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptAndVerify(String encryptedMessage,
                                   String receiverPassphrase,
                                   ArmoredKeyPair receiverArmoredKeyPair,
                                   String senderUserIdEmail,
                                   ArmoredKey senderArmoredPublicKey) {
        InMemoryKeyring keyring = keyring(receiverPassphrase, receiverArmoredKeyPair, senderArmoredPublicKey);
        ByteArrayOutputStream unencryptedOutputStream = new ByteArrayOutputStream();
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(unencryptedOutputStream);
             InputStream bouncyGPGInputStream = BouncyGPG
                     .decryptAndVerifyStream()
                     .withConfig(keyring)
                     .andRequireSignatureFromAllKeys(senderUserIdEmail)
                     .fromEncryptedInputStream(new ByteArrayInputStream(encryptedMessage.getBytes(UTF_8)))) {
            Streams.pipeAll(bouncyGPGInputStream, bufferedOutputStream);
        } catch (PGPException | NoSuchProviderException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return unencryptedOutputStream.toString(UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private InMemoryKeyring keyring(String passphrase,
                                    ArmoredKeyPair armoredKeyPair,
                                    ArmoredKey... recipientsArmoredPublicKey) {
        InMemoryKeyring keyring;
        try {
            keyring = KeyringConfigs.forGpgExportedKeys(KeyringConfigCallbacks.withPassword(passphrase));
        } catch (IOException | PGPException e) {
            throw new RuntimeException(e);
        }
        try {
            keyring.addSecretKey(armoredKeyPair.privateKey().key(UTF_8));
        } catch (IOException | PGPException e) {
            throw new RuntimeException(e);
        }
        try {
            keyring.addPublicKey(armoredKeyPair.publicKey().key(UTF_8));
        } catch (IOException | PGPException e) {
            throw new RuntimeException(e);
        }
        for (ArmoredKey recipientArmoredPublicKey : recipientsArmoredPublicKey) {
            try {
                keyring.addPublicKey(recipientArmoredPublicKey.key(UTF_8));
            } catch (IOException | PGPException e) {
                throw new RuntimeException(e);
            }
        }
        return keyring;
    }

}
