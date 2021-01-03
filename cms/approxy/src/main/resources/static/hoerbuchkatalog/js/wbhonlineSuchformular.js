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
                    const inputField = form.querySelector('input[type="text"][class*="form-control"]');
                    const sachgebiete = form.querySelector('select#sachgebiet');
                    const einstelldatum = form.querySelector('input#einstelldatum');
                    if ('' !== inputField.value || '-- keine Auswahl --' !== sachgebiete.value || '' !== einstelldatum.value) {
                        event.currentTarget.disabled = true;
                        const url = self.stichwortsucheUrl(inputField.value, sachgebiete.value, einstelldatum.value);
                        window.location = url.toString();
                    } else {
                        console.log(inputField.value + '--' + sachgebiete.value + '--' + einstelldatum.value);
                    }
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
        if (undefined !== sachgebiet && sachgebiet.length === 1) {
            url.search += '&sachgebiet=' + encodeURIComponent(sachgebiet);
        } else {
            url.search += '&sachgebiet=' + encodeURIComponent('*');
        }
        if (undefined !== einstelldatum && einstelldatum.length === 10) {
            url.search += '&einstelldatum=' + encodeURIComponent(einstelldatum);
        } else {
            url.search += '&einstelldatum=' + encodeURIComponent('*');
        }
        return url;
    }

    zeigeSuchwerte() {
        const searchParams = new URLSearchParams(window.location.search);
        if (searchParams.has('stichwort')) {
            const stichwort = decodeURIComponent(searchParams.get('stichwort'));
            let sachgebietValue = searchParams.get('sachgebiet');
            if (undefined === sachgebietValue) {
                sachgebietValue = '';
            }
            const sachgebiet = decodeURIComponent(sachgebietValue);
            let einstelldatumValue = searchParams.get('einstelldatum');
            if (undefined === einstelldatumValue) {
                einstelldatumValue = '';
            }
            const einstelldatum = decodeURIComponent(einstelldatumValue);
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
