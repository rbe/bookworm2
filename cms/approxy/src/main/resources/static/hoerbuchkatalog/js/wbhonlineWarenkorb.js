/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";
import {WbhonlineButtons} from "./wbhonlineButtons.js";
import {WbhonlineHelper} from "./wbhonlineHelper.js";

export class WbhonlineWarenkorb {

    constructor() {
        this.helper = new WbhonlineHelper();
        const [hoerernummer, bestellungSessionId] = this.helper.readCookie();
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer, bestellungSessionId);
        this.buttons = new WbhonlineButtons();
        this.initialize();
    }

    initialize() {
        const warenkorbButtons = document.querySelectorAll('a[id^="warenkorb-"]');
        for (const warenkorbButton of warenkorbButtons) {
            const imWarenkorb = warenkorbButton.classList.contains('order-cd-true');
            if (imWarenkorb) {
                this.inDenWarenkorbGelegt(warenkorbButton);
            } else {
                this.ausDemWarenkorbEntfernt(warenkorbButton);
            }
            WbhonlineButtons.addMultiEventListener(warenkorbButton, 'click touchstart', (event) => {
                this.buttons.disableAnchor(event.currentTarget);
                const titelnummer = this.helper.titelnummer(event.currentTarget);
                this.flipWarenkorbButton(titelnummer, warenkorbButton,
                    () => this.buttons.enableAnchor(warenkorbButton));
            });
        }
    }

    flipWarenkorbButton(titelnummer, warenkorbButton, callback) {
        const imWarenkorb = warenkorbButton.classList.contains('order-cd-true');
        if (imWarenkorb) {
            this.bookwormRestClient.entferneAusWarenkorb(titelnummer, () => {
                this.ausDemWarenkorbEntfernt(warenkorbButton);
            });
        } else {
            this.bookwormRestClient.fuegeZuWarenkorbHinzu(titelnummer, () => {
                this.inDenWarenkorbGelegt(warenkorbButton);
            });
        }
        if (undefined !== callback && null !== callback) {
            callback();
        }
    }

    inDenWarenkorbGelegt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-false');
        warenkorbButton.classList.add('order-cd-true');
        this.buttons.setTitle(warenkorbButton, 'CD aus der Bestellung entfernen');
    }

    ausDemWarenkorbEntfernt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-true');
        warenkorbButton.classList.add('order-cd-false');
        this.buttons.setTitle(warenkorbButton, 'Hörbuch als CD bestellen');
    }

}
