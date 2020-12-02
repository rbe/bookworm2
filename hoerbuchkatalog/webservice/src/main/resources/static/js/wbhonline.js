/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";

export class Wbhonline {

    constructor(hoerernummer) {
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer);
    }

    hoerprobeButtons() {
        const hoerprobeButtons = document.querySelectorAll('.button.hoerprobe-true');
        for (const hoerprobeButton of hoerprobeButtons) {
            hoerprobeButton.addEventListener('click', (event) => {
                const titelnummer = event.target.parentElement.id.split('-')[1];
                this.bookwormRestClient.hoerprobe(titelnummer);
            });
        }
    }

    merklisteButtons() {
        const merklisteButtons = document.querySelectorAll('a[id^="merkliste-"]');
        for (const merklisteButton of merklisteButtons) {
            merklisteButton.addEventListener('click', (event) => {
                const titelnummer = event.target.parentElement.id.split('-')[1];
                const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
                if (aufMerkliste) {
                    this.bookwormRestClient.entferneVonMerkliste(titelnummer, () => {
                        merklisteButton.classList.remove('watchlist-true');
                        merklisteButton.classList.add('watchlist-false');
                    });
                } else {
                    this.bookwormRestClient.fuegeZuMerklisteHinzu(titelnummer, () => {
                        merklisteButton.classList.remove('watchlist-false');
                        merklisteButton.classList.add('watchlist-true');
                    });
                }
            });
        }
    }

    warenkorbButtons() {
        const warenkorbButtons = document.querySelectorAll('a[id^="warenkorb-"]');
        for (const warenkorbButton of warenkorbButtons) {
            warenkorbButton.addEventListener('click', (event) => {
                const titelnummer = event.target.parentElement.id.split('-')[1];
                const imWarenkorb = warenkorbButton.classList.contains('order-cd-true');
                if (imWarenkorb) {
                    this.bookwormRestClient.entferneAusWarenkorb(titelnummer, () => {
                        warenkorbButton.classList.remove('order-cd-true');
                        warenkorbButton.classList.add('order-cd-false');
                    });
                } else {
                    this.bookwormRestClient.fuegeZuWarenkorbHinzu(titelnummer, () => {
                        warenkorbButton.classList.remove('order-cd-false');
                        warenkorbButton.classList.add('order-cd-true');
                    });
                }
            });
        }
    }

    downloadButtons() {
        const downloadButtons = document.querySelectorAll('a[id^="download-"]');
        for (const downloadButton of downloadButtons) {
            downloadButton.addEventListener('click', (event) => {
                const titelnummer = event.target.parentElement.id.split('-')[1];
                this.bookwormRestClient.bestelleDownload(titelnummer);
            });
        }
    }

}
