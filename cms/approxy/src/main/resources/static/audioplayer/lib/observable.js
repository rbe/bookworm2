/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class Observable {

    constructor() {
        this.observers = [];
    }

    subscribe(fn) {
        this.observers.push(fn);
    }

    unsubscribe(fn) {
        this.observers = this.observers.filter((observer) => observer !== fn);
    }

    notify(data) {
        this.observers.forEach(observer => observer(data));
    }

    /*
    fire(o, thisObj) {
        const scope = thisObj || window;
        this.observers.forEach(function (item) {
            item.call(scope, o);
        });
    }
    */

}
