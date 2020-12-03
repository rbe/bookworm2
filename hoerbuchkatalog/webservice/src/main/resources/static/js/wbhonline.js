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
        const hoerprobeButtons = document.querySelectorAll('a[id^="hoerprobe-"]');
        for (const hoerprobeButton of hoerprobeButtons) {
            if (hoerprobeButton.classList.contains('hoerprobe-true')) {
                hoerprobeButton.title = 'Hörprobe abspielen'
                hoerprobeButton.ariaLabel = 'Hörprobe abspielen';
                hoerprobeButton.addEventListener('click', (event) => {
                    const titelnummer = event.currentTarget.id.split('-')[1];
                    //const audio = document.querySelector('audio[id="audio-' + titelnummer + '"]');
                    const i = hoerprobeButton.querySelector('i');
                    this.bookwormRestClient.hoerprobe(titelnummer, null,
                        () => {
                            ['fas', 'fa-volume-up', 'fa'].forEach(value => i.classList.remove(value));
                            ['far', 'fa-pause-circle'].forEach(value => i.classList.add(value));
                        },
                        () => {
                            ['far', 'fa-pause-circle'].forEach(value => i.classList.remove(value));
                            ['fas', 'fa-volume-up', 'fa'].forEach(value => i.classList.add(value));
                        });
                });
            } else {
                hoerprobeButton.title = 'Hörprobe nicht verfügbar'
                hoerprobeButton.ariaLabel = 'Hörprobe nicht verfügbar';
            }
        }
    }

    merklisteButtons() {
        const merklisteButtons = document.querySelectorAll('a[id^="merkliste-"]');
        for (const merklisteButton of merklisteButtons) {
            merklisteButton.addEventListener('click', (event) => {
                const titelnummer = event.currentTarget.id.split('-')[1];
                const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
                if (aufMerkliste) {
                    this.bookwormRestClient.entferneVonMerkliste(titelnummer, () => {
                        merklisteButton.classList.remove('watchlist-true');
                        merklisteButton.classList.add('watchlist-false');
                        merklisteButton.title = 'Hörbuch auf die Merkliste setzen';
                        merklisteButton.ariaLabel = 'Hörbuch auf die Merkliste setzen';
                    });
                } else {
                    this.bookwormRestClient.fuegeZuMerklisteHinzu(titelnummer, () => {
                        merklisteButton.classList.remove('watchlist-false');
                        merklisteButton.classList.add('watchlist-true');
                        merklisteButton.title = 'Hörbuch von der Merkliste entfernen';
                        merklisteButton.ariaLabel = 'Hörbuch von der Merkliste entfernen';
                    });
                }
            });
        }
    }

    warenkorbButtons() {
        const warenkorbButtons = document.querySelectorAll('a[id^="warenkorb-"]');
        for (const warenkorbButton of warenkorbButtons) {
            warenkorbButton.addEventListener('click', (event) => {
                const titelnummer = event.currentTarget.id.split('-')[1];
                const imWarenkorb = warenkorbButton.classList.contains('order-cd-true');
                if (imWarenkorb) {
                    this.bookwormRestClient.entferneAusWarenkorb(titelnummer, () => {
                        warenkorbButton.classList.remove('order-cd-true');
                        warenkorbButton.classList.add('order-cd-false');
                        warenkorbButton.title = 'CD aus der Bestellung entfernen';
                        warenkorbButton.ariaLabel = 'CD aus der Bestellung entfernen';
                    });
                } else {
                    this.bookwormRestClient.fuegeZuWarenkorbHinzu(titelnummer, () => {
                        warenkorbButton.classList.remove('order-cd-false');
                        warenkorbButton.classList.add('order-cd-true');
                        warenkorbButton.title = 'Hörbuch als CD bestellen';
                        warenkorbButton.ariaLabel = 'Hörbuch als CD bestellen';
                    });
                }
            });
        }
    }

    downloadButtons() {
        const downloadButtons = document.querySelectorAll('a[id^="download-"]');
        for (const downloadButton of downloadButtons) {
            if (downloadButton.classList.contains('order-download-true')) {
                downloadButton.title = 'Hörbuch herunterladen';
                downloadButton.ariaLabel = 'Hörbuch herunterladen';
                downloadButton.addEventListener('click', (event) => {
                    const titelnummer = event.currentTarget.id.split('-')[1];
                    this.bookwormRestClient.bestelleDownload(titelnummer);
                });
            } else {
                downloadButton.title = 'Hörbuch nicht als Download verfügbar';
                downloadButton.ariaLabel = 'Hörbuch nicht als Download verfügbar';
            }
        }
    }

    onDomReady() {
        document.addEventListener('DOMContentLoaded', () => {
            this.hoerprobeButtons();
            this.merklisteButtons();
            this.warenkorbButtons();
            this.downloadButtons();
        });
    }

}
