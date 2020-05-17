/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {FetchErrorHandler} from "./lib/fetchErrorHandler.js";

export class Playlist {

    constructor(elementSelectors, audioplayer, audiobookURL, hoerernummer, titelnummer, onReadyCallback) {
        this.elementSelectors = elementSelectors;
        this.audioplayer = audioplayer;
        this.audiobookURL = audiobookURL;
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
        this.playlist = [];
        this.currentTrackIndex = -1;
        this.currentTrackTitle = document.querySelector(this.elementSelectors.currentTrackTitleSelector);
        window.EventBus.subscribe('previousTrack', event => this.previous);
        window.EventBus.subscribe('nextTrack', event => this.next);
        this.updatePlaylist(onReadyCallback);
    }

    updatePlaylist(onReadyCallback) {
        fetch(new URL('info/playlist', this.audiobookURL).toString(),
            {
                'method': 'POST',
                'headers': {
                    'Content-Type': 'application/json'
                },
                'body': JSON.stringify({
                    mandant: 'WBH',
                    hoerernummer: this.hoerernummer,
                    aghNummer: 'TODO',
                    titelnummer: this.titelnummer
                })
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .then(json => {
                if (json) {
                    this.playlist = json.entries;
                    this.highestTrackIndex = this.playlist.length - 1;
                    this.render();
                    if (onReadyCallback) {
                        onReadyCallback();
                    }
                }
            })
            .catch(reason => {
                console.log('Playlist#init,fetch,catch: ' + reason);
            });
    }

    render() {
        let playlistHtml = '<ul role="list" aria-label="Playlist">';
        for (let playlistIdx in this.playlist) {
            if (this.playlist.hasOwnProperty(playlistIdx)) {
                const playlistElement = this.playlist[playlistIdx];
                const playlistElementId = 'track-' + playlistIdx;
                this.playlist[playlistIdx].playlistElementId = playlistElementId;
                const title = playlistElement.title || playlistElement.ident;
                playlistHtml += '<li role="listitem" aria-label="' + title + '" id="' + playlistElementId + '">' + title;
                if (playlistElement.clips && playlistElement.clips.length > 0) {
                    playlistHtml += this.renderClips(playlistElement, playlistElement.clips);
                }
                playlistHtml += '</li>';
            }
        }
        playlistHtml += '</ul>';
        document.querySelector(this.elementSelectors.playlistSelector).innerHTML = playlistHtml;
        // hide clips
        const clipsList = document.querySelectorAll('div.playlist ul[aria-label="Clips"]');
        for (const clipsListItem of clipsList) {
            if (clipsListItem.style) clipsListItem.style.display = 'none';
        }
        const elements = document.querySelector(this.elementSelectors.playlistSelector + ' li ul');
        if (elements) elements.style.display = 'none';
        const self = this;
        const playlistElements = document.querySelectorAll(this.elementSelectors.playlistSelector + ' li');
        for (let idx = 0; idx < playlistElements.length; idx++) {
            const playlistElementId = playlistElements[idx].id;
            const playlistElementIdSelector = '#' + playlistElementId;
            document.querySelector(playlistElementIdSelector)
                .addEventListener('click', function (event) {
                    if (this === event.target) {
                        let {track, second} = Playlist.trackIdAndSecondFrom(playlistElementId);
                        if (undefined === second || isNaN(second)) second = 0;
                        self.audioplayer.selectTrack(track, second);
                        self.audioplayer.play(second);
                        // show clips on click
                        const elements = document.querySelector(playlistElementIdSelector + ' ul');
                        if (elements) {
                            elements.style.display = elements.style.display === 'none' ? 'block' : 'none';
                        }
                    }
                });
        }
    }

    renderClips(playlistElement, clips) {
        let clipsHtml = '<ul role="list" aria-label="Clips">';
        clips.forEach(element => {
            const clipId = Playlist.makeClipId(playlistElement.playlistElementId, element);
            clipsHtml += '<li role="listitem" aria-label="" id="' + clipId + '">' + element + '</li>'
        });
        clipsHtml += '</ul>';
        return clipsHtml;
    }

    count() {
        return this.playlist.length;
    }

    trackInfo(trackIndex) {
        return this.playlist[trackIndex];
    }

    previous() {
        if (this.currentTrackIndex > 0) {
            this.currentTrackIndex--;
        } else {
            this.currentTrackIndex = 1;
        }
        window.EventBus.notify('previousTrackSelected', {'trackIndex': this.currentTrackIndex});
    }

    next() {
        if (this.currentTrackIndex < this.highestTrackIndex) {
            this.currentTrackIndex++;
        } else {
            this.currentTrackIndex = this.highestTrackIndex;
        }
        window.EventBus.notify('nextTrackSelected', {'trackIndex': this.currentTrackIndex});
    }

    enableButtons() {
    }

    disableButtons() {
    }

    reset() {
        const playlistElement = document.querySelector(this.elementSelectors.playlistEntriesSelector);
        if (playlistElement) {
            playlistElement.remove();
        }
    }

    static trackIdAndSecondFrom(playlistElementId) {
        const eins = playlistElementId.substring('track-'.length);
        let idxDash = eins.indexOf('-');
        let track;
        if (idxDash > -1) {
            track = eins.substring(0, idxDash);
        } else {
            track = eins;
        }
        let second = 0;
        if (idxDash > -1) {
            second = parseFloat(eins.substring(eins.indexOf(track) + track.length + 1)
                .replace('--', '.'));
        }
        return {track, second};
    }

    static makeClipId(playlistElementId, clip) {
        return playlistElementId + '-'
            + clip.toString().replace('.', '--');
    }

}
