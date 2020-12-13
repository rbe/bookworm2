/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class FetchErrorHandler {

    static handle(response) {
        const status = response.status;
        if (status >= 200 && status < 400) {
            console.log('FetchErrorHandler#handle: OK -- HTTP status ' + response.status);
        } else if (status >= 400 && status < 500) {
            console.log('FetchErrorHandler#handle: Client Error -- HTTP status ' + response.status);
        } else if (status >= 500) {
            console.log('FetchErrorHandler#handle: FAILED -- HTTP status ' + response.status);
        } else {
            alert('Unhandled response status!');
        }
    }

}
