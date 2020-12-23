/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export const HOERER_UNBEKANNT = '00000';

export const LOGIN_HTML = '/anmelden.html';

export class WbhonlineHelper {

    //
    // Cookie
    //

    readCookie() {
        const bookwormCookie = document.cookie
            .split('; ')
            .find(row => row.startsWith('bookworm'))
            .split('=')[1];
        const strings = bookwormCookie.split('--');
        return [strings[0], strings[1]];
    }

    //
    // DOM
    //

    titelnummer(element) {
        return element.id.split('-')[1];
    }

}
