/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class FetchErrorHandler {

    constructor() {
    }

    static handle(response) {
        console.log('FetchErrorHandler#handle: HTTP status ' + response.status);
        const status = response.status;
        if (status >= 200 && status < 300) {
        } else if (status >= 400 && status < 500) {
        } else if (status >= 500) {
        } else {
            alert('Unhandled response status!');
        }
    }

}
