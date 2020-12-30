/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export const HOERER_UNBEKANNT = '00000';
export const LOGIN_HTML = '/anmelden.html';
export const HOERBUCHKATALOG_URL = 'https://www-prod.wbh-online.de';
export const SHARD_URLS = [
    'https://hoerbuchdienst-shard11.wbh-online.de',
    'https://hoerbuchdienst-shard12.wbh-online.de',
    'https://hoerbuchdienst-shard13.wbh-online.de'
];
export const DOWNLOAD_STATUS_TIMEOUT = 2500;

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
