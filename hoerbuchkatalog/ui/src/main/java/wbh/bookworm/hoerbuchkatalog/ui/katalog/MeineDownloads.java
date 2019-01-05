/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.BlistaDownload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequestScope
public class MeineDownloads {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineDownloads.class);

    private final HoererSession hoererSession;

    private final MeineBestellung meineBestellung;

    private String stichwort;

    private List<BlistaDownload> nachStichwortGefilterteDownloads;

    @Autowired
    public MeineDownloads(final HoererSession hoererSession,
                          final MeineBestellung meineBestellung) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        this.meineBestellung = meineBestellung;
        nachStichwortGefilterteDownloads = Collections.emptyList();
    }

    public boolean isStichwortEingegeben() {
        return null != stichwort && !stichwort.isBlank();
    }

    public String getStichwort() {
        return stichwort;
    }

    public void setStichwort(final String stichwort) {
        this.stichwort = stichwort;
        if (!isStichwortEingegeben()) {
            nachStichwortGefilterteDownloads = Collections.emptyList();
        }
    }

    public void sucheNachStichwort() {
        if (isStichwortEingegeben()) {
            nachStichwortGefilterteDownloads = hoererSession.alleDownloads()
                    .stream()
                    .filter(h -> h.getAutor().toLowerCase().contains(stichwort.toLowerCase())
                            || h.getTitel().toLowerCase().contains(stichwort.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            nachStichwortGefilterteDownloads = Collections.emptyList();
        }
    }

    public boolean isStichwortHatTreffer() {
        return isStichwortEingegeben()
                && !hoererSession.isBlistaAbrufHatFehler() && meineBestellung.isBestellungenVorhanden()
                && !nachStichwortGefilterteDownloads.isEmpty();
    }

    public List<BlistaDownload> getAlleDownloads() {
        return nachStichwortGefilterteDownloads.isEmpty()
                ? hoererSession.alleDownloads()
                : nachStichwortGefilterteDownloads;
    }

    public List<BlistaDownload> getBezugsfaehigeDownloads() {
        return nachStichwortGefilterteDownloads.isEmpty()
                ? hoererSession.bezugsfaehigeDownloads()
                : nachStichwortGefilterteDownloads;
    }

    public boolean isHoerbuecherAnzeigen() {
        return (!hoererSession.isBlistaAbrufHatFehler()
                //&& meineBestellung.isBestellungenVorhanden()
                && !hoererSession.bezugsfaehigeDownloads().isEmpty()
                && null == stichwort || stichwort.isBlank())
                || isStichwortHatTreffer();
    }

    public boolean isBlistaAbrufHatFehler() {
        return hoererSession.isBlistaAbrufHatFehler();
    }

    public String getBlistaFehlercode() {
        return hoererSession.blistaFehlercode();
    }

    public String getBlistaFehlermeldung() {
        return hoererSession.blistaFehlermeldung();
    }

    public boolean isHoerbucherAusgeliehen() {
        return !hoererSession.isBlistaAbrufHatFehler()
                //&& meineBestellung.isBestellungenVorhanden()
                && !hoererSession.bezugsfaehigeDownloads().isEmpty();
    }

    // TODO
    public void downloadLink(final BlistaDownload blistaDownload) throws IOException {
        LOGGER.info("Hörer {} lädt Hörbuch {} / {} herunter",
                hoererSession.getHoerernummer(),
                blistaDownload.getTitelnummer(), blistaDownload.getAghNummer());
        final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(blistaDownload.getDownloadLink());
    }

    // TODO
    public void downloadZaehlen(final ActionEvent actionEvent) {
        final HtmlCommandLink source = (HtmlCommandLink) actionEvent.getSource();
        final FacesContext context = FacesContext.getCurrentInstance();
        final BlistaDownload blistaDownload = FacesContext.getCurrentInstance().getApplication()
                .evaluateExpressionGet(context, "#{download}", BlistaDownload.class);
        blistaDownload.downloadCounterRuntersetzen();
        LOGGER.debug("{} -> {}", source, blistaDownload);
    }

    /* TODO BestellungAufgegebenEvent
    void bestellungAufgegeben() {
        hoererSession.downloadsVergessen();
    }
    */

}
