/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {WbhonlineButtons} from "./wbhonlineButtons.js";
import {HOERER_UNBEKANNT, LOGIN_HTML, WbhonlineHelper} from "./wbhonlineHelper.js";
import {BookwormRestClient} from "./bookwormRestClient.js";

export class WbhonlineMerkliste {

    constructor() {
        this.helper = new WbhonlineHelper();
        const [hoerernummer, bestellungSessionId] = this.helper.readCookie();
        this.bookwormRestClient = new BookwormRestClient('06', hoerernummer, bestellungSessionId);
        this.buttons = new WbhonlineButtons();
        this.initialize(hoerernummer);
    }

    initialize(hoerernummer) {
        const merklisteButtons = document.querySelectorAll('a[id^="merkliste-"]');
        for (const merklisteButton of merklisteButtons) {
            if (hoerernummer !== HOERER_UNBEKANNT) {
                const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
                if (aufMerkliste) {
                    this.zurMerklisteHinzugefuegt(merklisteButton);
                } else {
                    this.vonMerklisteEntfernt(merklisteButton);
                }
                const self = this;
                const merklisteHandleClick = function (event) {
                    self.buttons.disableAnchor(event.currentTarget);
                    const titelnummer = self.helper.titelnummer(event.currentTarget);
                    self.flipMerklisteButton(titelnummer, event.currentTarget,
                        () => self.buttons.enableAnchor(event.currentTarget));
                }
                WbhonlineButtons.addMultiEventListener(merklisteButton, 'click touchstart', merklisteHandleClick);
            } else {
                this.merklisteNichtEingeloggt(merklisteButton);
            }
        }
    }

    merklisteNichtEingeloggt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-true', 'watchlist-false');
        merklisteButton.classList.add('watchlist-login');
        merklisteButton.href = LOGIN_HTML;
        this.buttons.setTitle(merklisteButton, 'Merkliste nur für WBH-Hörer, bitte anmelden!');
    }

    flipMerklisteButton(titelnummer, merklisteButton, callback) {
        const aufMerkliste = merklisteButton.classList.contains('watchlist-true');
        if (aufMerkliste) {
            this.bookwormRestClient.entferneVonMerkliste(titelnummer, () => {
                // "Meine Merkliste": div entfernen
                // ansonsten: CSS-Klasse des Buttons ändern
                const div = document.querySelector('#merkliste-eintrag-' + titelnummer);
                if (undefined !== div && null !== div) {
                    div.remove();
                } else {
                    this.vonMerklisteEntfernt(merklisteButton, titelnummer);
                }
            });
        } else {
            this.bookwormRestClient.fuegeZuMerklisteHinzu(titelnummer, () => {
                this.zurMerklisteHinzugefuegt(merklisteButton);
            });
        }
        if (undefined !== callback && null !== callback) {
            callback();
        }
    }

    zurMerklisteHinzugefuegt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-false');
        merklisteButton.classList.add('watchlist-true');
        this.buttons.setTitle(merklisteButton, 'Hörbuch von der Merkliste entfernen');
    }

    vonMerklisteEntfernt(merklisteButton) {
        merklisteButton.classList.remove('watchlist-true');
        merklisteButton.classList.add('watchlist-false');
        this.buttons.setTitle(merklisteButton, 'Hörbuch auf die Merkliste setzen');
    }

    removeRow(titelnummer) {
        const div = document.querySelector('#merliste-eintrag-' + titelnummer);
        if (undefined !== div && null !== div) {
            div.remove();
        }
    }

}
