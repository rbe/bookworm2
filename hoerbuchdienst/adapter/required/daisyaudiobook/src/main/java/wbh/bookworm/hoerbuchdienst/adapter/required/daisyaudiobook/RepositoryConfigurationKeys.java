/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

class RepositoryConfigurationKeys {

    static final String HOERBUCHDIENST_REPOSITORY_TYPE = "hoerbuchdienst.repository.type";

    static final String HOERBUCHDIENST_REPOSITORY_LOCALDISK_URI = "hoerbuchdienst.repository.localdisk.uri";

    static final String HOERBUCHDIENST_REPOSITORY_OBJECTSTORAGE_NAME = "hoerbuchdienst.repository.objectstorage.name";

    static final String HOERBUCHDIENST_TEMPORARY_PATH = "hoerbuchdienst.repository.localdisk.temporary.path";

    private RepositoryConfigurationKeys() {
        throw new AssertionError();
    }

}
