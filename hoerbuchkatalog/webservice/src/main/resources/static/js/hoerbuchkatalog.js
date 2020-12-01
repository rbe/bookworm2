/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./lib/bookwormRestClient.js";

document.addEventListener("DOMContentLoaded", function () {
    const bookworm = new BookwormRestClient('06', '00000');
    // Hörprobe
    const hoerprobeButton = document.querySelector('.button.hoerprobe-true');
    if (hoerprobeButton) {
        hoerprobeButton.addEventListener('click', (e) => {
            const titelnummer = e.target.parentElement.id.split('-')[1];
            bookworm.hoerprobe(titelnummer);
        });
    }
    // Merkliste
    const merklisteButtons = document.querySelectorAll('a[id^="merkliste-"]');
    for (const merklisteButton of merklisteButtons) {
        merklisteButton.addEventListener('click', (e) => {
            const titelnummer = e.target.parentElement.id.split('-')[1];
            const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
            if (aufMerkliste) {
                bookworm.entferneVonMerkliste(titelnummer, () => {
                    merklisteButton.classList.remove('watchlist-true');
                    merklisteButton.classList.add('watchlist-false');
                });
            } else {
                bookworm.fuegeZuMerklisteHinzu(titelnummer, () => {
                    merklisteButton.classList.remove('watchlist-false');
                    merklisteButton.classList.add('watchlist-true');
                });
            }
        });
    }
    // Warenkorb
    const warenkorbHinzufuegenButton = document.querySelector('.button.order-cd-false');
    if (warenkorbHinzufuegenButton) {
        warenkorbHinzufuegenButton.addEventListener('click', function (e) {
            console.log(e);
            bookworm.fuegeZuWarenkorbHinzu();
        });
    }
    const warenkorbEntfernenButton = document.querySelector('.button.order-cd-true');
    if (warenkorbEntfernenButton) {
        warenkorbEntfernenButton.addEventListener('click', function (e) {
            const titelnummer = e.target.parentElement.id.split('-')[1];
            bookworm.entferneAusWarenkorb(titelnummer);
        });
    }
    // Download
    const downloadButton = document.querySelector('.button .order-download-true');
    if (downloadButton) {
        downloadButton.addEventListener('click', () => {
            const titelnummer = e.target.parentElement.id.split('-')[1];
            bookworm.bestelleDownload(titelnummer);
        });
    }
});
