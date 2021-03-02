/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {WbhonlineButtons} from "./wbhonlineButtons.js";

const STICHWORTSUCHE_URL = '/stichwortsuche.html';

export class WbhonlineSuchformular {

    constructor() {
        this.zeigeSuchwerte();
        this.suchformular();
    }

    suchformular() {
        const button = document.querySelector('button#suchen');
        const enterListener = (event) => {
            if (event.keyCode === 13) {
                event.preventDefault();
                button.click();
            }
        };
        const inputField = document.querySelector('input#stichwort');
        if (undefined !== inputField && null !== inputField) {
            inputField.addEventListener('keyup', enterListener);
        }
        const inputTitel = document.querySelector('input#titel');
        if (undefined !== inputTitel && null !== inputTitel) {
            inputTitel.addEventListener('keyup', enterListener);
        }
        const inputAutor = document.querySelector('input#autor');
        if (undefined !== inputAutor && null !== inputAutor) {
            inputAutor.addEventListener('keyup', enterListener);
        }
        const inputEinstelldatum = document.querySelector('input#einstelldatum');
        if (undefined !== inputEinstelldatum && null !== inputEinstelldatum) {
            inputEinstelldatum.addEventListener('keyup', enterListener);
        }
        const inputStartdatum = document.querySelector('input#startdatum');
        if (undefined !== inputStartdatum && null !== inputStartdatum) {
            inputStartdatum.addEventListener('keyup', enterListener);
        }
        if (undefined !== button && null !== button) {
            const self = this;
            const buttonListener = function (event) {
                event.currentTarget.disabled = true;
                const inputField = document.querySelector('input#stichwort');
                const sachgebiete = document.querySelector('select#sachgebiet');
                const einstelldatum = document.querySelector('input#einstelldatum');
                const startdatum = document.querySelector('input#startdatum');
                const titel = document.querySelector('input#titel');
                const autor = document.querySelector('input#autor');
                const url = self.erstelleUrl(inputField, sachgebiete, einstelldatum, startdatum, titel, autor);
                window.location = url.toString();
            };
            WbhonlineButtons.addMultiEventListener(button, 'click touchend', buttonListener);
        }
    }

    erstelleUrl(stichwort, sachgebiet, einstelldatum, startdatum, titel, autor) {
        const url = new URL(window.location);
        switch (url.pathname) {
            case '/konto/bestellkarte.html':
            case '/konto/ausleihe.html':
            case '/konto/archiv.html':
                break;
            default:
                url.pathname = STICHWORTSUCHE_URL;
                break;
        }
        url.searchParams.delete('stichwort');
        url.searchParams.delete('sachgebiet');
        url.searchParams.delete('einstelldatum');
        url.searchParams.delete('startdatum');
        url.searchParams.delete('titel');
        url.searchParams.delete('autor');
        url.search = Array.from(url.searchParams.entries())
            .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
            .join('&');
        if (url.search.length > 0) {
            url.search += '&';
        }
        if (this.isset(stichwort)) {
            url.search += 'stichwort=' + this.encode(stichwort);
        }
        if (this.isset(sachgebiet)) {
            url.search += '&sachgebiet=' + this.encode(sachgebiet, 1, 1);
        }
        if (this.isset(einstelldatum)) {
            url.search += '&einstelldatum=' + this.encode(einstelldatum, 10, 10);
        }
        if (this.isset(startdatum)) {
            url.search += '&startdatum=' + this.encode(startdatum, 10, 10);
        }
        if (this.isset(titel)) {
            url.search += '&titel=' + this.encode(titel, 1, 100);
        }
        if (this.isset(autor)) {
            url.search += '&autor=' + this.encode(autor, 1, 100);
        }
        return url;
    }

    encode(obj, minLength = 1, maxLength = 1000) {
        if (undefined !== obj && null !== obj) {
            if ('' !== obj.value && obj.value.length >= minLength && obj.value.length <= maxLength) {
                return encodeURIComponent(obj.value);
            }
        }
        return '*';
    }

    zeigeSuchwerte() {
        const searchParams = new URLSearchParams(window.location.search);
        const stichwort = this.decode(searchParams.get('stichwort'));
        this.setValue(document, 'input#stichwort', stichwort);
        const sachgebiet = this.decode(searchParams.get('sachgebiet'));
        this.setValue(document, 'select#sachgebiet', sachgebiet);
        const einstelldatum = this.decode(searchParams.get('einstelldatum'));
        this.setValue(document, 'input#einstelldatum', einstelldatum);
        const startdatum = this.decode(searchParams.get('startdatum'));
        this.setValue(document, 'input#startdatum', startdatum);
        const titel = this.decode(searchParams.get('titel'));
        this.setValue(document, 'input#titel', titel);
        const autor = this.decode(searchParams.get('autor'));
        this.setValue(document, 'input#autor', autor);
    }

    decode(val, def = '') {
        return undefined !== val && null !== val ? decodeURIComponent(val) : def;
    }

    setValue(form, selector, value) {
        if (this.isset(value)) {
            const element = form.querySelector(selector);
            if (undefined !== element && null !== element) {
                element.value = value;
            }
        }
    }

    isset(value) {
        return null !== value && '' !== value && '*' !== value;
    }

}
