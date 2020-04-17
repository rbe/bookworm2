/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.objectstorage;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface BucketObjectStorage {

    List<Path> listAll();

    List<Path> listObjects(String prefix);

    void put(String objectName, InputStream stream, String contentType);

    boolean objectExists(String objectName);

    InputStream asStream(String objectName);

    byte[] asBytes(String objectName);

    InputStream zip(String dirName);

}
