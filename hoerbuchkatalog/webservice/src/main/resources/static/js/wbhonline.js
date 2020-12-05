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

const PLAY_BUTTON = ['fas', 'fa-volume-up', 'fa'];
const PAUSE_BUTTON = ['far', 'fa-pause-circle'];

export class Wbhonline {

    constructor(mandant, hoerernummer) {
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer);
        this.audioplayer = new Audioplayer(SHARD_URL, mandant, hoerernummer);
    }

    hoerprobeButtons() {
        const hoerprobeButtons = document.querySelectorAll('a[id^="hoerprobe-"]');
        for (const hoerprobeButton of hoerprobeButtons) {
            if (hoerprobeButton.classList.contains('hoerprobe-true')) {
                hoerprobeButton.title = 'Hörprobe abspielen'
                hoerprobeButton.ariaLabel = 'Hörprobe abspielen';
                hoerprobeButton.addEventListener('click', (event) => {
                    this.disableAnchor(event.currentTarget);
                    const titelnummer = event.currentTarget.id.split('-')[1];
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
                            this.enableAnchor(event.currentTarget);
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
    }

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
                const titelnummer = event.currentTarget.id.split('-')[1];
                this.flipMerklisteButton(titelnummer, merklisteButton);
            });
        }
    }

    flipMerklisteButton(titelnummer, merklisteButton) {
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
    }

    zurMerklisteHinzugefuegt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-false');
        merklisteButton.classList.add('watchlist-true');
        merklisteButton.title = 'Hörbuch von der Merkliste entfernen';
        merklisteButton.ariaLabel = 'Hörbuch von der Merkliste entfernen';
        this.enableAnchor(merklisteButton);
    }

    vonMerklisteEntfernt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-true');
        merklisteButton.classList.add('watchlist-false');
        merklisteButton.title = 'Hörbuch auf die Merkliste setzen';
        merklisteButton.ariaLabel = 'Hörbuch auf die Merkliste setzen';
        this.enableAnchor(merklisteButton);
    }

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
                const titelnummer = event.currentTarget.id.split('-')[1];
                this.flipWarenkorbButton(titelnummer, warenkorbButton);
            });
        }
    }

    flipWarenkorbButton(titelnummer, warenkorbButton) {
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
    }

    ausDemWarenkorbEntfernt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-false');
        warenkorbButton.classList.add('order-cd-true');
        warenkorbButton.title = 'Hörbuch als CD bestellen';
        warenkorbButton.ariaLabel = 'Hörbuch als CD bestellen';
        this.enableAnchor(warenkorbButton);
    }

    inDenWarenkorbGelegt(warenkorbButton) {
        warenkorbButton.classList.remove('order-cd-true');
        warenkorbButton.classList.add('order-cd-false');
        warenkorbButton.title = 'CD aus der Bestellung entfernen';
        warenkorbButton.ariaLabel = 'CD aus der Bestellung entfernen';
        this.enableAnchor(warenkorbButton);
    }

    downloadButtons() {
        const downloadButtons = document.querySelectorAll('a[id^="download-"]');
        for (const downloadButton of downloadButtons) {
            if (downloadButton.classList.contains('order-download-true')) {
                downloadButton.title = 'Hörbuch herunterladen';
                downloadButton.ariaLabel = 'Hörbuch herunterladen';
                downloadButton.addEventListener('click', (event) => {
                    this.disableAnchor(event.currentTarget);
                    const titelnummer = event.currentTarget.id.split('-')[1];
                    this.bookwormRestClient.bestelleDownload(titelnummer, () => {
                        this.enableAnchor(event.currentTarget);
                    });
                });
            } else {
                downloadButton.title = 'Hörbuch nicht als Download verfügbar';
                downloadButton.ariaLabel = 'Hörbuch nicht als Download verfügbar';
            }
        }
    }

    disableAnchor(anchor) {
        anchor.classList.add('disabled');
    }

    enableAnchor(anchor) {
        anchor.classList.remove('disabled');
    }

    onDomReady() {
        document.addEventListener('DOMContentLoaded', () => {
            this.merklisteButtons();
            this.warenkorbButtons();
            this.downloadButtons();
            this.hoerprobeButtons();
        });
    }

}
