/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

const stichwortsucheUrl = '/stichwortsuche.html';

export class WbhonlineSuchformular {

    initialisiereSuchformulare() {
        const forms = document.querySelectorAll('form[id^="catalogsearch-"]');
        for (const form of forms) {
            const inputField = form.querySelector('input[type="text"][class*="form-control"]');
            if (undefined !== inputField) {
                const button = form.querySelector('button[class*="search"]');
                inputField.addEventListener('keyup', (event) => {
                    if (event.keyCode === 13) {
                        event.preventDefault();
                        button.click();
                    }
                });
                if (undefined !== button && null !== button) {
                    button.addEventListener('click', (event) => {
                        event.currentTarget.disabled = true;
                        const url = new URL(window.location);
                        url.pathname = stichwortsucheUrl;
                        url.searchParams.delete('stichwort');
                        url.search = Array.from(url.searchParams.entries())
                            .map(([k, v]) =>
                                `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
                            .join('&');
                        if (url.search.length > 0) {
                            url.search += '&';
                        }
                        url.search += 'stichwort=' + encodeURIComponent(inputField.value);
                        window.location = url.toString();
                    });
                }
            }
        }
    }

    zeigeStichwortNachSuche() {
        const searchParams = new URLSearchParams(window.location.search);
        if (searchParams.has('stichwort')) {
            const stichwort = decodeURIComponent(searchParams.get('stichwort'));
            document.title = 'WBH: Suche nach ' + stichwort;
            const forms = document.querySelectorAll('form[id^="catalogsearch-"]');
            for (const form of forms) {
                const inputField = form.querySelector('input[type="text"][class*="form-control"]');
                if (undefined !== inputField && null !== inputField) {
                    inputField.value = stichwort;
                }
            }
        }
    }

    initialize() {
        this.zeigeStichwortNachSuche();
        this.initialisiereSuchformulare();
    }

}

document.addEventListener('DOMContentLoaded', (event) => {
    const suchformular = new WbhonlineSuchformular();
    suchformular.initialize();
});
