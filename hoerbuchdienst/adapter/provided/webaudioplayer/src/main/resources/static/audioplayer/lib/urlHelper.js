/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

class UrlHelper {

    static filename(url) {
        const pathname = url.pathname;
        return pathname.substring(pathname.lastIndexOf('/') + 1);
    }

    static obfuscateMp3(url) {
        return "data:audio/mp3;base64," + btoa(url);
    }

}
