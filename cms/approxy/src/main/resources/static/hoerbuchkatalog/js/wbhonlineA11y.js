/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class WbhonlineA11y {

    fireAccesskey(element) {
        element.addEventListener('click', (event) => {
            event.preventDefault();
            element.scrollIntoViewIfNeeded();
            element.focus();
        });
    }

    setupAccesskeys() {
        const elements = document.querySelectorAll('a[accesskey]');
        for (const elt of elements) {
            this.fireAccesskey(elt);
        }
    }

}
