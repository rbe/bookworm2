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
            // no error
        } else if (status >= 400 && status < 500) {
            alert('Client error, HTTP status ' + response.status);
        } else if (status >= 500 && status <= 511) {
            alert('Server error, HTTP status ' + response.status);
        } else {
            alert('Unbekannter HTTP Status ' + response.status);
        }
    }

}
