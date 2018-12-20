/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsRestConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DlsBestellung {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlsBestellung.class);

    private final DlsRestConfig dlsRestConfig;

    private final Auftragsuebermittlung auftragsuebermittlung;

    @Autowired
    DlsBestellung(final DlsRestConfig dlsRestConfig,
                  final Auftragsuebermittlung auftragsuebermittlung) {
        this.dlsRestConfig = dlsRestConfig;
        this.auftragsuebermittlung = auftragsuebermittlung;
    }

    /**
     * Prüfen, ob eine Bestellung Erfolg haben kann
     */
    private boolean pruefen(final String userId, final String aghNummer) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s/%s",
                        dlsRestConfig.getUrl(), userId, aghNummer)))
                .header("bibliothek", dlsRestConfig.getBibliothek())
                .header("bibkennwort", dlsRestConfig.getBibkennwort())
                .build();
        final String result = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();
        return false;
    }

    private Auftragsquittung bestellen(final String userId, final String aghNummer) {
        Auftragsquittung auftragsquittung = null;
        try {
            final String abrufkennwort = auftragsuebermittlung.uebergeben(userId, aghNummer);
            auftragsquittung = new Auftragsquittung(abrufkennwort, Auftragsstatus.NEU.name());
        } catch (Exception e) {
            LOGGER.error(String.format(
                    "Konnte Bestellung für Benutzer %s, AGH Nummer %s nicht aufgeben", userId, aghNummer),
                    e);
        }
        return auftragsquittung;
    }

    public List<Auftragsquittung> pruefenUndBestellen(final String userId,
                                                      final String... aghNummern) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(aghNummern);
        MDC.put("userId", userId);
        final List<Auftragsquittung> auftragsquittungen = new LinkedList<>();
        try {
            auftragsquittungen.addAll(Arrays.stream(aghNummern)
                    .map(aghNummer -> {
                        MDC.put("aghNummer", aghNummer);
                        pruefen(userId, aghNummer);
                        return bestellen(userId, aghNummer);
                    })
                    .collect(Collectors.toList()));
        } finally {
            MDC.clear();
        }
        return auftragsquittungen;
    }

    public Auftragsstatus auftragsstatus(final String userId, final String aghNummer) {
        MDC.put("userId", userId);
        MDC.put("aghNummer", aghNummer);
        try {
            return auftragsuebermittlung.auftragsstatus(userId, aghNummer);
        } finally {
            MDC.clear();
        }
    }

}
