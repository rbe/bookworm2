/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class WbhonlineSuchformular {

    suchFormulare() {
        const forms = document.querySelectorAll('form[id^="catalogsearch-"]');
        for (const form of forms) {
            const inputField = form.querySelector('input[type="text"][class*="form-control"]');
            const button = form.querySelector('button[class*="search"]');
            if (button !== undefined && button !== null) {
                button.addEventListener('click', (event) => {
                    event.currentTarget.disabled = true;
                    const url = new URL(window.location);
                    url.pathname = '/konto/stichwortsuche.html';
                    url.searchParams.set('stichwort', inputField.value);
                    window.location = url.toString();
                });
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
                if (inputField !== undefined && inputField !== null) {
                    inputField.value = stichwort;
                }
            }
        }
    }

    initialize() {
        this.zeigeStichwortNachSuche();
        this.suchFormulare();
    }

}

document.addEventListener('DOMContentLoaded', (event) => {
    const suchformular = new WbhonlineSuchformular();
    suchformular.initialize();
});
