/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {WbhonlineButtons} from "./wbhonlineButtons.js";

const stichwortsucheUrl = '/stichwortsuche.html';

class WbhonlineSuchformular {

    suchformular() {
        const forms = document.querySelectorAll('div[class="catalogsearch"]');
        for (const form of forms) {
            const button = form.querySelector('button[class*="search"]');
            const enterListener = (event) => {
                if (event.keyCode === 13) {
                    event.preventDefault();
                    button.click();
                }
            };
            const inputField = form.querySelector('input[type="text"][class*="form-control"]');
            if (undefined !== inputField && null !== inputField) {
                inputField.addEventListener('keyup', enterListener);
            }
            if (undefined !== button && null !== button) {
                const self = this;
                const buttonListener = function (event) {
                    event.currentTarget.disabled = true;
                    const inputField = form.querySelector('input[type="text"][class*="form-control"]');
                    const sachgebiete = form.querySelector('select#sachgebiet');
                    const einstelldatum = form.querySelector('input#einstelldatum');
                    const url = self.stichwortsucheUrl(inputField, sachgebiete, einstelldatum);
                    window.location = url.toString();
                };
                WbhonlineButtons.addMultiEventListener(button, 'click touchend', buttonListener);
            }
        }
    }

    stichwortsucheUrl(stichwort, sachgebiet, einstelldatum) {
        const url = new URL(window.location);
        url.pathname = stichwortsucheUrl;
        url.searchParams.delete('stichwort');
        url.searchParams.delete('sachgebiet');
        url.searchParams.delete('einstelldatum');
        url.search = Array.from(url.searchParams.entries())
            .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
            .join('&');
        if (url.search.length > 0) {
            url.search += '&';
        }
        url.search += 'stichwort=' + this.encode(stichwort);
        url.search += '&sachgebiet=' + this.encode(sachgebiet);
        url.search += '&einstelldatum=' + this.encode(einstelldatum, 10);
        return url;
    }

    encode(obj, minLength = 1) {
        if (undefined !== obj && null !== obj) {
            if ('' !== obj.value && obj.value.length >= minLength) {
                return encodeURIComponent(obj.value);
            }
        }
        return '';
    }

    zeigeSuchwerte() {
        const searchParams = new URLSearchParams(window.location.search);
        const stichwort = this.decode(searchParams.get('stichwort'));
        const sachgebiet = this.decode(searchParams.get('sachgebiet'));
        const einstelldatum = this.decode(searchParams.get('einstelldatum'));
        document.title = 'WBH: Suche - Stichwort: ' + stichwort
            + ' Sachgebiet: ' + sachgebiet
            + ' Einstelldatum: ' + einstelldatum;
        const forms = document.querySelectorAll('div[class="catalogsearch"]');
        for (const form of forms) {
            this.setValue(form, 'input[type="text"][class="form-control"]', stichwort);
            this.setValue(form, 'select#sachgebiet', sachgebiet);
            this.setValue(form, 'input#einstelldatum', einstelldatum);
        }
    }

    decode(val, def = '') {
        return undefined !== val && null !== val ? decodeURIComponent(val) : def;
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
    const suchformular = new WbhonlineSuchformular();
    suchformular.initialisiere();
});
