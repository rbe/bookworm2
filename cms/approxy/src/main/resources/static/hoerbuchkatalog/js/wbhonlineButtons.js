/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

const ENABLE_SPINNER = ['fas', 'fa-spinner', 'fa-spin'];

export class WbhonlineButtons {

    constructor() {
        this.previousClassList = new Map();
    }

    setTitle(button, text) {
        button.title = text;
        button.ariaLabel = text;
    }

    withButtons(fun, skipSelector = '', skipArray = []) {
        document.querySelectorAll('a[class*="button"]').forEach(element => {
            const matchesSkipSelector = skipSelector !== '' && element.matches(skipSelector);
            const inSkipArray = typeof skipArray !== 'undefined' && skipArray.length > 0 && !skipArray.includes(element);
            if (!matchesSkipSelector && !inSkipArray) {
                fun(element);
            }
        });
    }

    disableButtons(skipSelector = '', skipArray = []) {
        this.withButtons((element) => this.disableAnchor(element), '', []);
    }

    enableButtons(skipSelector = '', skipArray = []) {
        this.withButtons((element) => this.enableAnchor(element), '', []);
    }

    disableAnchor(anchor) {
        anchor.style.pointerEvents = 'none';
    }

    enableAnchor(anchor) {
        anchor.style.pointerEvents = '';
    }

    activateSpinner(i) {
        i.style.pointerEvents = 'none';
        this.previousClassList.set(i.id, [...i.classList]);
        i.className = '';
        ENABLE_SPINNER.forEach(value => i.classList.add(value));
    }

    deactivateSpinner(i) {
        if (this.previousClassList.has(i.id)) {
            i.className = '';
            this.previousClassList.get(i.id).forEach(value => i.classList.add(value));
        }
        i.style.pointerEvents = '';
    }

    static addMultiEventListener(elt, events, fn = null) {
        events.split(' ').forEach(event => elt.addEventListener(event, (e) => {
            e.preventDefault();
            if (null != fn) {
                fn(e);
            }
        }, false));
    }

}
