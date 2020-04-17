/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.objectstorage;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class HashTools {

    private HashTools() {
        throw new AssertionError();
    }

    static String md5(Path path) {
        try {
            final byte[] bytes = Files.readAllBytes(path);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(bytes);
            byte[] digest = md5.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            //System.out.println(DigestUtils.md5Hex(bytes));
            return hashtext;
        } catch (IOException e) {
            return "";
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

}
