/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";
import {WbhonlineButtons} from "./wbhonlineButtons.js";
import {HOERBUCHKATALOG_URL, WbhonlineHelper} from "./wbhonlineHelper.js";

export class WbhonlineCdBestellung {

    constructor() {
        this.helper = new WbhonlineHelper();
        const [hoerernummer, bestellungSessionId] = this.helper.readCookie();
        this.bestellungSessionId = bestellungSessionId;
        this.mandant = '06';
        this.bookwormRestClient = new BookwormRestClient(this.mandant, hoerernummer, bestellungSessionId);
        this.buttons = new WbhonlineButtons();
        this.initialize();
    }

    initialize() {
        const entfernenButtons = document.querySelectorAll('a[id^="cdBestellung-"]');
        for (const entfernenButton of entfernenButtons) {
            WbhonlineButtons.addMultiEventListener(entfernenButton, 'click touchstart', this.removeRow());
        }
        const bestellungSendenButton = document.querySelector('button[class*="bestellung-absenden"]');
        if (undefined !== bestellungSendenButton && null !== bestellungSendenButton) {
            const self = this;
            const sendeBestellformular = function (event) {
                const hoerernummer = self.formValue('hoerernummer');
                if (undefined === hoerernummer || hoerernummer.trim() === '') {
                    alert('Bitte die Hörernummer eingeben!');
                    return;
                }
                const hoereremail = self.formValue('email');
                if (undefined === hoereremail || hoereremail.trim() === '') {
                    alert('Bitte eine E-Mail Adrese eingeben!');
                    return;
                }
                const url = new URL('/hoerbuchkatalog/v1/warenkorb', HOERBUCHKATALOG_URL);
                const bestellung = {
                    'hoerername': self.formValue('nachname'),
                    'hoereremail': hoereremail,
                    'bemerkung': self.formValue('bemerkung'),
                    'bestellkarteMischen': self.formValue('bestellkarteMischen'),
                    'alteBestellkarteLoeschen': self.formValue('alteBestellkarteLoeschen')
                };
                fetch(url.toString(), {
                    'method': 'POST',
                    'headers': {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'X-Bookworm-Mandant': self.mandant,
                        'X-Bookworm-Hoerernummer': hoerernummer,
                        'X-Bookworm-BestellungSessionId': self.bestellungSessionId
                    },
                    'body': JSON.stringify(bestellung)
                })
                    .then(response => {
                        if (response.ok) {
                            alert('Bestellung erfolgreich aufgegeben!');
                        } else {
                            alert('Leider konnte die Bestellung nicht aufgegeben werden!');
                        }
                    })
                    .catch(reason => {
                        alert('Fehler: ' + reason);
                    });
            }
            WbhonlineButtons.addMultiEventListener(bestellungSendenButton, 'click touchstart', sendeBestellformular);
        }
    }

    removeRow() {
        return (event) => {
            this.buttons.disableAnchor(event.currentTarget);
            const titelnummer = this.helper.titelnummer(event.currentTarget);
            this.bookwormRestClient.entferneAusWarenkorb(titelnummer, () => {
                const div = document.querySelector('div[id="cdbestellung-' + titelnummer + '"]');
                div.remove();
            });
        };
    }

    formValue(name) {
        const form = document.querySelector('div.warenkorb-besteller div.' + name + '-content');
        const inputField = form.querySelector('input[type="text"]');
        if (undefined !== inputField && null !== inputField) {
            return inputField.value;
        }
        const textarea = form.querySelector('textarea');
        if (undefined !== textarea && null !== textarea) {
            return textarea.value;
        }
        const checkbox = form.querySelector('input[type="checkbox"]');
        if (undefined !== checkbox && null !== checkbox) {
            return checkbox.checked;
        }
        return '';
    }

}
