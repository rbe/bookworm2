/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {WbhonlineButtons} from "./wbhonlineButtons";

export class WbhonlineHoererarchiv {

    suchformular() {
        const forms = document.querySelectorAll('div[id*="suche-"]');
        for (const form of forms) {
            const button = form.querySelector('button[class*="search"]');
            const enterListener = (event) => {
                if (event.keyCode === 13) {
                    event.preventDefault();
                    button.click();
                }
            };
            const inputStichwort = form.querySelector('input#stichwort');
            if (undefined !== inputStichwort && null !== inputStichwort) {
                inputStichwort.addEventListener('keyup', enterListener);
            }
            const inputStartdatum = form.querySelector('input#startdatum');
            if (undefined !== inputStartdatum && null !== inputStartdatum) {
                inputStartdatum.addEventListener('keyup', enterListener);
            }
            if (undefined !== button && null !== button) {
                const buttonListener = (event) => {
                    if ('' !== inputStichwort.value || '' !== inputStartdatum.value) {
                        event.currentTarget.disabled = true;
                        const url = this.stichwortsucheUrl(inputStichwort.value, inputStartdatum.value);
                        window.location = url.toString();
                    }
                };
                WbhonlineButtons.addMultiEventListener(button, 'click touchstart', buttonListener);
            }
        }
    }

    stichwortsucheUrl(stichwort, startdatum) {
        const url = new URL(window.location);
        url.pathname = window.location.pathname;
        url.searchParams.delete('stichwort');
        url.searchParams.delete('startdatum');
        url.search = Array.from(url.searchParams.entries())
            .map(([k, v]) =>
                `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
            .join('&');
        if (url.search.length > 0) {
            url.search += '&';
        }
        if (undefined !== stichwort && stichwort !== '') {
            url.search += 'stichwort=' + encodeURIComponent(stichwort);
        } else {
            url.search += 'stichwort=' + encodeURIComponent('*');
        }
        if (undefined !== startdatum && startdatum.length === 10) {
            url.search += '&startdatum=' + encodeURIComponent(startdatum);
        } else {
            url.search += '&startdatum=' + encodeURIComponent('*');
        }
        return url;
    }

    zeigeSuchwerte() {
        const searchParams = new URLSearchParams(window.location.search);
        if (searchParams.has('stichwort')) {
            let stichwortValue = searchParams.get('stichwort');
            if (undefined === stichwortValue) {
                stichwortValue = '';
            }
            const stichwort = decodeURIComponent(stichwortValue);
            let startdatumValue = searchParams.get('startdatum');
            if (undefined === startdatumValue) {
                startdatumValue = '';
            }
            const startdatum = decodeURIComponent(startdatumValue);
            document.title = 'WBH: Suche - Stichwort: ' + stichwort
                + ' Startdatum: ' + startdatum;
            const forms = document.querySelectorAll('div[id*="suche-"]');
            for (const form of forms) {
                this.setValue(form, 'input#stichwort', stichwort);
                this.setValue(form, 'input#startdatum', startdatum);
            }
        }
    }

    setValue(form, selector, value) {
        if (null !== value && value !== '' && value !== '*') {
            const element = form.querySelector(selector);
            if (undefined !== element && null !== element) {
                element.value = value;
            }
        }
    }

    initialisiere() {
        this.zeigeSuchwerte();
        this.suchformular();
    }

}

document.addEventListener('DOMContentLoaded', (event) => {
    const suchformular = new WbhonlineHoererarchiv();
    suchformular.initialisiere();
});
