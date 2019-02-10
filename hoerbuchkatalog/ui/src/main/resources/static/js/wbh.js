/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

$(document).ready(function ($) {
    $("div.logout").click(function () {
        $.when(
            $.get("/hoerbuchkatalog/logout", function (data) {
                console.log("Catalog logout was performed.");
            }),
            // $.get("/nutzerbereich/logout", function (data) {
            //     console.log("Customer logout was performed.");
            // }),
            $.get("/index.php?option=com_comprofiler&task=logout", function (data) {
                console.log("Joomla logout was performed.");
            })
        ).then(
            function () {
                console.log("data/redirect");
                window.location.replace("/");
            },
            function () {
                console.log("error/redirect");
                window.location.replace("/");
            }
        );
    });
});
