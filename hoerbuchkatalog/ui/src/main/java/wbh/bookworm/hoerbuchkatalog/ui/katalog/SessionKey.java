/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

public final class SessionKey {

    public static final String HOERERNUMMER = "hnr";

    public static final String HOERER = "hoerer";

    public static final String BESTELLUNG_SESSION_ID = "bestellungSessionId";

    public static final String HOERBUCH = "HOERBUCH";

    public static final String SCOPEDTARGET_HOERERSESSION = "scopedTarget.hoererSession";

    private SessionKey() {
        throw new AssertionError();
    }

}
