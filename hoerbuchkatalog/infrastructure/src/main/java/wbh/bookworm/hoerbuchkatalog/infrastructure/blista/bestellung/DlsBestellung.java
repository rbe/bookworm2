/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsAntwort;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsResponse;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.DlsRestConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.RestServiceClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
    Auftragsquittung pruefen(final String userId, final String aghNummer) {
        final HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(String.format("%s/%s/%s",
                        dlsRestConfig.getCheckurl(), userId, aghNummer)))
                .header("bibliothek", String.valueOf(dlsRestConfig.getBibliothek()))
                .header("bibkennwort", String.valueOf(dlsRestConfig.getBibkennwort()))
                .timeout(Duration.ofSeconds(5))
                .build();
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
                .sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    final DlsAntwort dlsAntwort = RestServiceClient.werteAntwortAus(response.body());
                    if (dlsAntwort instanceof DlsResponse) {
                        final DlsResponse dlsResponse = (DlsResponse) dlsAntwort;
                        return new Auftragsquittung(aghNummer,
                                !dlsResponse.hatFehler() && dlsResponse.isSuccess());
                    } else {
                        LOGGER.warn("Unbekannte Antwort bei Prüfung für Hörer {} und AGH Nummer {} erhalten",
                                userId, aghNummer);
                        return new Auftragsquittung(aghNummer, false);
                    }
                })
                .getNow(new Auftragsquittung(aghNummer, false));
    }

    public List<Auftragsquittung> pruefenUndBestellen(final String userId,
                                                      final String... aghNummern) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(aghNummern);
        MDC.put("userId", userId);
        LOGGER.trace("Prüfe und bestelle die AGH Nummern {} für Benutzer {}",
                aghNummern, userId);
        final List<Auftragsquittung> auftragsquittungen = new LinkedList<>();
        try {
            auftragsquittungen.addAll(Arrays.stream(aghNummern)
                    .map(aghNummer -> {
                        MDC.put("aghNummer", aghNummer);
                        final Auftragsquittung auftragsquittung = pruefen(userId, aghNummer);
                        if (auftragsuebermittlung.uebergeben(userId, aghNummer)) {
                            LOGGER.debug("Bestellung der AGH Nummer {} für Benutzer {}" +
                                            " erfolgreich geprüft und übergeben",
                                    aghNummern, userId);
                            auftragsquittung.uebermittlungOk();
                        } else {
                            LOGGER.error("Prüfung der AGH Nummer {} für Benutzer {} war ok," +
                                            " die Übermittlung leider erfolglos",
                                    aghNummern, userId);
                        }
                        return auftragsquittung;
                    })
                    .collect(Collectors.toList()));
            LOGGER.info("AGH Nummern {} für Benutzer {} geprüft und bestellt: {}",
                    aghNummern, userId, auftragsquittungen);
        } finally {
            MDC.clear();
        }
        return auftragsquittungen;
    }

}
