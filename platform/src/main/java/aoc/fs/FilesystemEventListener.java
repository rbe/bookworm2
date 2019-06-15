/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.fs;

import java.nio.file.Path;

public interface FilesystemEventListener {

    void processFilesystemEvent(Path path);

}
