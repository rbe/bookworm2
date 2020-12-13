/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";
import {Wbhbuttons} from "./wbhbuttons.js";
import {Audioplayer} from "./audioplayer.js";

const PLAY_BUTTON = ['fas', 'fa-volume-up', 'fa'];
const PAUSE_BUTTON = ['far', 'fa-pause-circle'];

const HOERER_UNBEKANNT = '00000';

export class Wbhonline {

    constructor() {
        this.readCookie();
        this.bookwormRestClient = new BookwormRestClient('06', this.hoerernummer,
            this.bestellungSessionId);
        this.audioplayer = new Audioplayer('06', this.hoerernummer);
        this.wbhbuttons = new Wbhbuttons();
    }

    readCookie() {
        const bookwormCookie = document.cookie
            .split('; ')
            .find(row => row.startsWith('bookworm'))
            .split('=')[1];
        const strings = bookwormCookie.split('--');
        this.hoerernummer = strings[0];
        this.bestellungSessionId = strings[1];
    }

    //
    // Merkliste
    //

    merklisteButtons() {
        const merklisteButtons = document.querySelectorAll('a[id^="merkliste-"]');
        for (const merklisteButton of merklisteButtons) {
            if (this.hoerernummer !== HOERER_UNBEKANNT) {
                const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
                if (aufMerkliste) {
                    this.zurMerklisteHinzugefuegt(merklisteButton);
                } else {
                    this.vonMerklisteEntfernt(merklisteButton);
                }
                merklisteButton.addEventListener('click', (event) => {
                    this.wbhbuttons.disableAnchor(event.currentTarget);
                    const titelnummer = this.titelnummer(event.currentTarget);
                    this.flipMerklisteButton(titelnummer, merklisteButton,
                        () => this.wbhbuttons.enableAnchor(merklisteButton));
                });
            } else {
                this.wbhbuttons.setTitle(merklisteButton, 'Bitte anmelden');
            }
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
        if (undefined !== callback) {
            callback();
        }
    }

    zurMerklisteHinzugefuegt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-false');
        merklisteButton.classList.add('watchlist-true');
        this.wbhbuttons.setTitle(merklisteButton, 'Hörbuch von der Merkliste entfernen');
    }

    vonMerklisteEntfernt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-true');
        merklisteButton.classList.add('watchlist-false');
        this.wbhbuttons.setTitle(merklisteButton, 'Hörbuch auf die Merkliste setzen');
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
                this.wbhbuttons.disableAnchor(event.currentTarget);
                const titelnummer = this.titelnummer(event.currentTarget);
                this.flipWarenkorbButton(titelnummer, warenkorbButton,
                    () => this.wbhbuttons.enableAnchor(warenkorbButton));
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
        if (undefined !== callback) {
            callback();
        }
    }

    inDenWarenkorbGelegt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-false');
        warenkorbButton.classList.add('order-cd-true');
        this.wbhbuttons.setTitle(warenkorbButton, 'CD aus der Bestellung entfernen');
    }

    ausDemWarenkorbEntfernt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-true');
        warenkorbButton.classList.add('order-cd-false');
        this.wbhbuttons.setTitle(warenkorbButton, 'Hörbuch als CD bestellen');
    }

    //
    // Hörprobe
    //

    hoerprobeButtons() {
        const hoerprobeButtons = document.querySelectorAll('a[id^="hoerprobe-"]');
        for (const hoerprobeButton of hoerprobeButtons) {
            if (hoerprobeButton.classList.contains('hoerprobe-true')) {
                this.wbhbuttons.setTitle(hoerprobeButton, 'Hörprobe abspielen');
                hoerprobeButton.addEventListener('click', (event) => {
                    this.wbhbuttons.disableButtons();
                    const i = hoerprobeButton.querySelector('i');
                    const titelnummer = this.titelnummer(event.currentTarget);
                    if (i.classList.contains('fa-volume-up')) {
                        this.activateSpinner(i);
                        this.audioplayer.spieleHoerprobeAb(titelnummer, null, event.currentTarget,
                            (element) => {
                                const i = element.querySelector('i');
                                this.wbhbuttons.deactivateSpinner(i);
                                this.hoerprobeSpielt(i);
                                this.wbhbuttons.enableAnchor(element);
                            },
                            (element) => {
                                const i = element.querySelector('i');
                                this.wbhbuttons.deactivateSpinner(i);
                                this.hoerprobePausiert(i);
                                this.wbhbuttons.enableButtons();
                            });
                    } else {
                        this.audioplayer.pausiereHoerprobe(event.currentTarget, (element) => {
                            const i = element.querySelector('i');
                            this.hoerprobePausiert(i);
                            this.wbhbuttons.deactivateSpinner(i);
                            this.wbhbuttons.enableButtons();
                        });
                    }
                });
            } else {
                this.wbhbuttons.setTitle(hoerprobeButton, 'Hörprobe nicht verfügbar');
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
    }

    //
    // Download
    //

    downloadButtons() {
        const downloadButtons = document.querySelectorAll('a[id^="download-"]');
        for (const downloadButton of downloadButtons) {
            if (this.hoerernummer !== HOERER_UNBEKANNT) {
                if (downloadButton.classList.contains('order-download-true')) {
                    this.wbhbuttons.setTitle(downloadButton, 'Hörbuch herunterladen');
                    downloadButton.addEventListener('click', (event) => {
                        this.disableButtons();
                        this.activateSpinner(event.currentTarget.querySelector('i'));
                        const titelnummer = this.titelnummer(event.currentTarget);
                        this.bookwormRestClient.bestelleDownload(titelnummer, event.currentTarget, (element) => {
                            this.deactivateSpinner(element.querySelector('i'));
                            this.enableButtons();
                        });
                    });
                } else {
                    this.wbhbuttons.setTitle(downloadButton, 'Hörbuch nicht als Download verfügbar');
                }
            } else {
                this.wbhbuttons.setTitle(downloadButton, 'Bitte anmelden');
            }
        }
    }

    //
    // DOM
    //

    titelnummer(element) {
        return element.id.split('-')[1];
    }

    initialize() {
        this.merklisteButtons();
        this.downloadButtons();
        this.warenkorbButtons();
        this.hoerprobeButtons();
    }

}
