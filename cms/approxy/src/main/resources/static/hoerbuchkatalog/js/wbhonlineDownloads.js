/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";
import {WbhonlineButtons} from "./wbhonlineButtons.js";
import {HOERER_UNBEKANNT, WbhonlineHelper} from "./wbhonlineHelper.js";

export class WbhonlineDownloads {

    constructor() {
        this.helper = new WbhonlineHelper();
        const [hoerernummer, bestellungSessionId] = this.helper.readCookie();
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer, bestellungSessionId);
        this.buttons = new WbhonlineButtons();
        this.initialiize(hoerernummer);
    }

    initialiize(hoerernummer) {
        const downloadButtons = document.querySelectorAll('a[id^="download-"]');
        for (const downloadButton of downloadButtons) {
            if (hoerernummer !== HOERER_UNBEKANNT) {
                if (downloadButton.classList.contains('order-download-true')) {
                    this.buttons.setTitle(downloadButton, 'Hörbuch herunterladen');
                    const self = this;
                    const bestelleDownload = function (event) {
                        self.buttons.disableButtons();
                        self.buttons.activateSpinner(event.currentTarget.querySelector('i'));
                        const titelnummer = self.helper.titelnummer(event.currentTarget);
                        self.bookwormRestClient.bestelleDownload(titelnummer, event.currentTarget,
                            (element) => {
                                self.buttons.deactivateSpinner(element.querySelector('i'));
                                self.buttons.enableButtons();
                            });
                    }
                    downloadButton.addEventListener('click', bestelleDownload);
                } else {
                    this.buttons.setTitle(downloadButton, 'Hörbuch nicht als Download verfügbar');
                }
            } else {
                this.downloadNichtEingeloggt(downloadButton);
            }
        }
    }

    downloadNichtEingeloggt(downloadButton) {
        downloadButton.classList.remove('order-download-true', 'order-download-false');
        downloadButton.classList.add('order-download-login');
        downloadButton.href = LOGIN_HTML;
        this.buttons.setTitle(downloadButton, 'Download nur für WBH-Hörer, bitte anmelden!');
    }

}
