/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Hoerprobe} from "./hoerprobe.js";
import {FetchErrorHandler} from "./fetchErrorHandler.js";

const hoerbuchkatalogURL = 'https://www.beta.wbh-online.de';
const shardURL = 'https://hoerbuchdienst.shard4.audiobook.wbh-online.de';

export class Bookworm {

    constructor(mandant, hoerernummer = '00000') {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.audioplayer = new Hoerprobe(shardURL, mandant, hoerernummer);
    }

    fuegeZuMerklisteHinzu(titelnummer) {
        const url = new URL('v1/merkliste/' + titelnummer, hoerbuchkatalogURL);
        fetch(url.toString(), {
            'method': 'POST',
            'headers': {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer
            },
            'redirect': 'follow'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .then(json => {
                if (json.result === 'true') {

                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    entferneVonMerkliste(titelnummer) {
    }

    fuegeZuWarenkorbHinzu(titelnummer) {
    }

    entferneAusWarenkorb(titelnummer) {
    }

    hoerprobe(titelnummer) {
        this.audioplayer.hoerprobe(titelnummer);
    }

    bestelleDownload(titelnummer) {
    }

}
