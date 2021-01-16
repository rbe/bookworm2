/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";
import {WbhonlineButtons} from "./wbhonlineButtons.js";
import {HOERER_UNBEKANNT, LOGIN_HTML, WbhonlineHelper} from "./wbhonlineHelper.js";

export class WbhonlineDownloads {

    constructor() {
        this.helper = new WbhonlineHelper();
        const [hoerernummer, bestellungSessionId] = this.helper.readCookie();
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer, bestellungSessionId);
        this.buttons = new WbhonlineButtons();
        this.initialize(hoerernummer);
    }

    initialize(hoerernummer) {
        const downloadButtons = document.querySelectorAll('a[id^="download-"]');
        for (const downloadButton of downloadButtons) {
            if (hoerernummer !== HOERER_UNBEKANNT) {
                if (downloadButton.classList.contains('downloaderlaubt-false')) {
                    this.nichtMoeglich(downloadButton);
                } else if (downloadButton.classList.contains('order-download-true')) {
                    this.verfuegbar(downloadButton);
                } else if (downloadButton.classList.contains('order-download-false')) {
                    this.nichtVerfuegbar(downloadButton);
                }
            } else {
                this.nichtEingeloggt(downloadButton);
            }
        }
    }

    verfuegbar(downloadButton) {
        this.buttons.setTitle(downloadButton, 'Hörbuch herunterladen');
        const self = this;
        const bestelleDownload = function (event) {
            self.buttons.disableButtons();
            const fontAwesomeElt = event.currentTarget.querySelector('i');
            self.buttons.activateSpinner(fontAwesomeElt);
            const titelnummer = self.helper.titelnummer(event.currentTarget);
            self.bookwormRestClient.bestelleDownload(titelnummer, event.currentTarget,
                (element) => {
                    const fontAwesomeElt = element.querySelector('i');
                    self.buttons.deactivateSpinner(fontAwesomeElt);
                    self.buttons.enableButtons();
                });
        }
        WbhonlineButtons.addMultiEventListener(downloadButton, 'click touchstart', bestelleDownload);
    }

    nichtVerfuegbar(downloadButton) {
        this.buttons.setTitle(downloadButton, 'Hörbuch nicht als Download verfügbar');
        WbhonlineButtons.addMultiEventListener(downloadButton, 'click touchstart');
    }

    nichtMoeglich(downloadButton) {
        downloadButton.classList.remove('order-download-true', 'order-download-false');
        this.buttons.setTitle(downloadButton, 'Sie haben die maximale Anzahl an Downloads erreicht!');
        WbhonlineButtons.addMultiEventListener(downloadButton, 'click touchstart');
    }

    nichtEingeloggt(downloadButton) {
        downloadButton.classList.remove('order-download-true', 'order-download-false');
        downloadButton.classList.add('order-download-login');
        downloadButton.href = LOGIN_HTML;
        this.buttons.setTitle(downloadButton, 'Download nur für WBH-Hörer, bitte anmelden!');
    }

}
