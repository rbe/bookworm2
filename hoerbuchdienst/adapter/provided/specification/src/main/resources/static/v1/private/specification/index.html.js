/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

window.onload = function () {
    const swagger_view_urls = {
        'swagger-ui': '/v1/private/specification/swagger-ui.html',
        'redoc': '/v1/private/specification/redoc.html',
        'rapidoc': '/v1/private/specification/rapidoc.html'
    };
    this.view_url = swagger_view_urls['swagger-ui'];
    this.service = '';
    // services
    const services = document.querySelectorAll('div#service div');
    for (const service of services) {
        service.addEventListener('click', (e) => {
            for (const service of services) {
                service.style = 'background: white';
            }
            e.target.style = 'color: white; background: black';
            this.service = e.target.id;
            if (this.view_url && this.service) {
                const content = document.querySelector('#content');
                content.src = this.view_url + '?service=' + this.service;
            }
        });
    }
    // swagger-view
    const swagger_views = document.querySelectorAll('div#swagger-view div');
    for (const swagger_view of swagger_views) {
        swagger_view.addEventListener('click', (e) => {
            for (const swagger_view of swagger_views) {
                swagger_view.style = 'background: white';
            }
            e.target.style = 'color: white; background: black';
            this.view_url = swagger_view_urls[swagger_view.id];
            if (this.view_url && this.service) {
                const content = document.querySelector('#content');
                content.src = this.view_url + '?service=' + this.service;
            }
        })
    }
};
