/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class DomEventBus {

    static setup(events) {
        window.EventBus = new DomEventBus();
        window.DomEventBus = {'Events': events};
    }

    constructor() {
        this.bus = document.createElement('theEventbus');
    }

    subscribe(event, callback) {
        this.bus.addEventListener(event, callback);
    }

    unsubscribe(event, callback) {
        this.bus.removeEventListener(event, callback);
    }

    notfy(event, detail = {}) {
        this.bus.dispatchEvent(new CustomEvent(event, {detail}));
    }

}
