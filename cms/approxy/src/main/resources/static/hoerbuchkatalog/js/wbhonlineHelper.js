/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export const HOERER_UNBEKANNT = '00000';
export const LOGIN_HTML = '/anmelden.html';
export const CD_BESTELLUNG_OK_HTML = '/cd-bestellung-ok.html';
export const CD_BESTELLUNG_FEHLER_HTML = '/cd-bestellung-fehler.html';
const host = window.location.host.split('.');
host.splice(0,1,'hoerbuchkatalog');
export const HOERBUCHKATALOG_URL = window.location.protocol + host.join('.');
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
            .find(row => row.startsWith('bookworm'));
        if (undefined !== bookwormCookie && null !== bookwormCookie) {
            const values = bookwormCookie.split('=')[1];
            const strings = values.split('--');
            return [strings[0], strings[1]];
        } else {
            return ['', ''];
        }
    }

    //
    // DOM
    //

    titelnummer(element) {
        return element.id.split('-')[1];
    }

}
