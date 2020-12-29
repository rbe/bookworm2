/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";
import {WbhonlineButtons} from "./wbhonlineButtons.js";
import {WbhonlineHelper} from "./wbhonlineHelper.js";

export class WbhonlineCdBestellung {

    constructor() {
        this.helper = new WbhonlineHelper();
        const [hoerernummer, bestellungSessionId] = this.helper.readCookie();
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer, bestellungSessionId);
        this.buttons = new WbhonlineButtons();
        this.initialize();
    }

    initialize() {
        const cdBestellungButtons = document.querySelectorAll('a[id^="cdBestellung-"]');
        for (const cdBestellungButton of cdBestellungButtons) {
            cdBestellungButton.addEventListener('click', this.removeRow());
        }
    }

    removeRow() {
        return (event) => {
            this.buttons.disableAnchor(event.currentTarget);
            const titelnummer = this.helper.titelnummer(event.currentTarget);
            this.bookwormRestClient.entferneAusWarenkorb(titelnummer, () => {
                const div = document.querySelector('div[id="cdbestellung-' + titelnummer + '"]');
                div.remove();
            });
        };
    }

}
