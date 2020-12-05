/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";
import {Audioplayer} from "./audioplayer.js";

const SHARD_URLS = [
    'https://hoerbuchdienst.shard1.audiobook.wbh-online.de',
    'https://hoerbuchdienst.shard2.audiobook.wbh-online.de',
    'https://hoerbuchdienst.shard3.audiobook.wbh-online.de',
    'https://hoerbuchdienst.shard4.audiobook.wbh-online.de'
];
const SHARD_URL = 'https://hoerbuchdienst.shard4.audiobook.wbh-online.de';

const ENABLE_SPINNER = ['fas', 'fa-spinner', 'fa-spin'];

const PLAY_BUTTON = ['fas', 'fa-volume-up', 'fa'];
const PAUSE_BUTTON = ['far', 'fa-pause-circle'];

export class Wbhonline {

    constructor(mandant, hoerernummer) {
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer);
        this.audioplayer = new Audioplayer(SHARD_URL, mandant, hoerernummer);
        this.previousClassList = new Map();
    }

    //
    // Merkliste
    //

    merklisteButtons() {
        const merklisteButtons = document.querySelectorAll('a[id^="merkliste-"]');
        for (const merklisteButton of merklisteButtons) {
            const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
            if (aufMerkliste) {
                this.zurMerklisteHinzugefuegt(merklisteButton);
            } else {
                this.vonMerklisteEntfernt(merklisteButton);
            }
            merklisteButton.addEventListener('click', (event) => {
                this.disableAnchor(event.currentTarget);
                const titelnummer = this.titelnummer(event.currentTarget);
                this.flipMerklisteButton(titelnummer, merklisteButton, () => this.enableAnchor(merklisteButton));
            });
        }
    }

    flipMerklisteButton(titelnummer, merklisteButton, callback) {
        const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
        if (aufMerkliste) {
            this.bookwormRestClient.entferneVonMerkliste(titelnummer, () => {
                this.vonMerklisteEntfernt(merklisteButton);
            });
        } else {
            this.bookwormRestClient.fuegeZuMerklisteHinzu(titelnummer, () => {
                this.zurMerklisteHinzugefuegt(merklisteButton);
            });
        }
        if (callback) {
            callback();
        }
    }

    zurMerklisteHinzugefuegt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-false');
        merklisteButton.classList.add('watchlist-true');
        merklisteButton.title = 'Hörbuch von der Merkliste entfernen';
        merklisteButton.ariaLabel = 'Hörbuch von der Merkliste entfernen';
    }

    vonMerklisteEntfernt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-true');
        merklisteButton.classList.add('watchlist-false');
        merklisteButton.title = 'Hörbuch auf die Merkliste setzen';
        merklisteButton.ariaLabel = 'Hörbuch auf die Merkliste setzen';
    }

    //
    // Warenkorb
    //

    warenkorbButtons() {
        const warenkorbButtons = document.querySelectorAll('a[id^="warenkorb-"]');
        for (const warenkorbButton of warenkorbButtons) {
            const imWarenkorb = warenkorbButton.classList.contains('order-cd-true');
            if (imWarenkorb) {
                this.inDenWarenkorbGelegt(warenkorbButton);
            } else {
                this.ausDemWarenkorbEntfernt(warenkorbButton);
            }
            warenkorbButton.addEventListener('click', (event) => {
                this.disableAnchor(event.currentTarget);
                const titelnummer = this.titelnummer(event.currentTarget);
                this.flipWarenkorbButton(titelnummer, warenkorbButton, () => this.enableAnchor(warenkorbButton));
            });
        }
    }

    flipWarenkorbButton(titelnummer, warenkorbButton, callback) {
        const imWarenkorb = warenkorbButton.classList.contains('order-cd-true');
        if (imWarenkorb) {
            this.bookwormRestClient.entferneAusWarenkorb(titelnummer, () => {
                this.inDenWarenkorbGelegt(warenkorbButton);
            });
        } else {
            this.bookwormRestClient.fuegeZuWarenkorbHinzu(titelnummer, () => {
                this.ausDemWarenkorbEntfernt(warenkorbButton);
            });
        }
        if (callback) {
            callback();
        }
    }

    ausDemWarenkorbEntfernt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-true');
        warenkorbButton.classList.add('order-cd-false');
        warenkorbButton.title = 'Hörbuch als CD bestellen';
        warenkorbButton.ariaLabel = 'Hörbuch als CD bestellen';
    }

    inDenWarenkorbGelegt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-false');
        warenkorbButton.classList.add('order-cd-true');
        warenkorbButton.title = 'CD aus der Bestellung entfernen';
        warenkorbButton.ariaLabel = 'CD aus der Bestellung entfernen';
    }

    //
    // Hörprobe
    //

    hoerprobeButtons() {
        const hoerprobeButtons = document.querySelectorAll('a[id^="hoerprobe-"]');
        for (const hoerprobeButton of hoerprobeButtons) {
            if (hoerprobeButton.classList.contains('hoerprobe-true')) {
                hoerprobeButton.title = 'Hörprobe abspielen'
                hoerprobeButton.ariaLabel = 'Hörprobe abspielen';
                hoerprobeButton.addEventListener('click', (event) => {
                    this.disableAllButtons();
                    const titelnummer = this.titelnummer(event.currentTarget);
                    const i = hoerprobeButton.querySelector('i');
                    if (i.classList.contains('fa-volume-up')) {
                        this.audioplayer.spieleHoerprobeAb(titelnummer, null,
                            () => {
                                this.hoerprobeSpielt(i);
                            },
                            () => {
                                this.hoerprobePausiert(i);
                            });
                    } else {
                        this.audioplayer.pausiereHoerprobe(() => {
                            this.hoerprobePausiert(i);
                            this.enableAllButtons();
                        });
                    }
                });
            } else {
                hoerprobeButton.title = 'Hörprobe nicht verfügbar'
                hoerprobeButton.ariaLabel = 'Hörprobe nicht verfügbar';
            }
        }
    }

    hoerprobeSpielt(i) {
        PLAY_BUTTON.forEach(value => i.classList.remove(value));
        PAUSE_BUTTON.forEach(value => i.classList.add(value));
    }

    hoerprobePausiert(i) {
        PAUSE_BUTTON.forEach(value => i.classList.remove(value));
        PLAY_BUTTON.forEach(value => i.classList.add(value));
        this.enableAllButtons();
    }

    //
    // Download
    //

    downloadButtons() {
        const downloadButtons = document.querySelectorAll('a[id^="download-"]');
        for (const downloadButton of downloadButtons) {
            if (downloadButton.classList.contains('order-download-true')) {
                downloadButton.title = 'Hörbuch herunterladen';
                downloadButton.ariaLabel = 'Hörbuch herunterladen';
                downloadButton.addEventListener('click', (event) => {
                    this.disableAllButtons();
                    this.activateSpinner(event.currentTarget);
                    const titelnummer = this.titelnummer(event.currentTarget);
                    this.bookwormRestClient.bestelleDownload(titelnummer, () => {
                        this.enableAllButtons();
                        this.deactivateSpinner(event.currentTarget);
                    });
                });
            } else {
                downloadButton.title = 'Hörbuch nicht als Download verfügbar';
                downloadButton.ariaLabel = 'Hörbuch nicht als Download verfügbar';
            }
        }
    }

    titelnummer(element) {
        let titelnummer = element.id.split('-')[1];
        if (titelnummer.length === 4) {
            titelnummer = '0' + titelnummer;
        }
        return titelnummer;
    }

    //
    // Buttons
    //

    disableAllButtons() {
        document.querySelectorAll('a[class*="button"]').forEach(element => {
            this.disableAnchor(element);
        })
    }

    enableAllButtons() {
        document.querySelectorAll('a[class*="button"]').forEach(element => {
            this.enableAnchor(element);
        })
    }

    disableAnchor(anchor) {
        anchor.style.pointerEvents = 'none';
    }

    enableAnchor(anchor) {
        anchor.style.pointerEvents = '';
    }

    activateSpinner(anchor) {
        anchor.style.pointerEvents = 'none';
        this.previousClassList.set(anchor.id, anchor.classList);
        anchor.classList.forEach(value => anchor.classList.remove(value));
        ENABLE_SPINNER.forEach(value => anchor.classList.add(value));
    }

    deactivateSpinner(anchor) {
        anchor.classList.forEach(value => anchor.classList.remove(value));
        if (this.previousClassList.has(anchor.id)) {
            this.previousClassList.get(anchor.id).forEach(value => anchor.classList.add(value));
        }
        anchor.style.pointerEvents = '';
    }

    //
    // DOM
    //

    onDomReady() {
        document.addEventListener('DOMContentLoaded', () => {
            this.merklisteButtons();
            this.warenkorbButtons();
            this.downloadButtons();
            this.hoerprobeButtons();
        });
    }

}
