/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {WbhonlineButtons} from "./wbhonlineButtons.js";
import {WbhonlineHelper} from "./wbhonlineHelper.js";
import {Audioplayer} from "./audioplayer.js";

const PLAY_BUTTON = ['fas', 'fa-volume-up', 'fa'];
const PAUSE_BUTTON = ['far', 'fa-pause-circle'];

export class WbhonlineHoerprobe {

    constructor() {
        this.helper = new WbhonlineHelper();
        const [hoerernummer, bestellungSessionId] = this.helper.readCookie();
        this.audioplayer = new Audioplayer('06', hoerernummer);
        this.buttons = new WbhonlineButtons();
        this.initialize();
    }

    initialize() {
        const hoerprobeButtons = document.querySelectorAll('a[id^="hoerprobe-"]');
        for (const hoerprobeButton of hoerprobeButtons) {
            if (hoerprobeButton.classList.contains('hoerprobe-true')) {
                this.buttons.setTitle(hoerprobeButton, 'Hörprobe abspielen');
                const self = this;
                const spieleOderPausiereHoerprobe = function (event) {
                    self.buttons.disableButtons();
                    const i = event.currentTarget.querySelector('i');
                    const titelnummer = self.helper.titelnummer(event.currentTarget);
                    if (i.classList.contains('fa-volume-up')) {
                        self.buttons.activateSpinner(i);
                        self.audioplayer.spieleHoerprobeAb(titelnummer, null, event.currentTarget,
                            (anchor) => {
                                const i = anchor.querySelector('i');
                                self.buttons.deactivateSpinner(i);
                                self.hoerprobeSpielt(i);
                                self.buttons.enableAnchor(anchor);
                            },
                            (anchor) => {
                                const i = anchor.querySelector('i');
                                self.buttons.deactivateSpinner(i);
                                self.hoerprobePausiert(i);
                                self.buttons.enableButtons();
                            });
                    } else {
                        self.audioplayer.pausiereHoerprobe(event.currentTarget, (anchor) => {
                            const i = anchor.querySelector('i');
                            self.hoerprobePausiert(i);
                            self.buttons.deactivateSpinner(i);
                            self.buttons.enableButtons();
                        });
                    }
                };
                hoerprobeButton.addEventListener('click', spieleOderPausiereHoerprobe);
            } else {
                this.buttons.setTitle(hoerprobeButton, 'Hörprobe nicht verfügbar');
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

}
