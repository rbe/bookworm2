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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequestScope
public class MeineDownloads {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineDownloads.class);

    private final HoererSession hoererSession;

    private final Stichwortsuche<BlistaDownload> stichwortsuche;

    @Autowired
    public MeineDownloads(final HoererSession hoererSession) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        stichwortsuche = new Stichwortsuche<>(hoererSession.bezugsfaehigeDownloads());
    }

    public String getStichwort() {
        return stichwortsuche.getStichwort();
    }

    public void setStichwort(final String stichwort) {
        stichwortsuche.setStichwort(stichwort);
    }

    public boolean isStichwortEingegeben() {
        return stichwortsuche.isStichwortEingegeben();
    }

    public void sucheNachStichwort() {
        LOGGER.debug("Suche nach Stichwort '{}'", stichwortsuche.getStichwort());
        stichwortsuche.sucheNachStichwort((blistaDownload, s) ->
                blistaDownload.getTitel().toLowerCase().contains(s.toLowerCase())
                        || blistaDownload.getAutor().toLowerCase().contains(s.toLowerCase()));
    }

    public void sucheVergessen() {
        stichwortsuche.stichwortVergessen();
    }

    public boolean isStichwortHatTreffer() {
        return stichwortsuche.isStichwortHatTreffer();
    }

    public boolean isHoerbuecherAnzeigen() {
        return (!hoererSession.isBlistaAbrufHatFehler()
                && !hoererSession.bezugsfaehigeDownloads().isEmpty())
                || isStichwortHatTreffer();
    }

    public boolean isBlistaAbrufHatFehler() {
        return hoererSession.isBlistaAbrufHatFehler()
                && !hoererSession.blistaFehlercode().equals("202");
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

    public String getStandVomAufDeutsch() {
        final LocalDateTime standVom = hoererSession.standVomDerDownloads();
        return null != standVom
                ? standVom.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public List<BlistaDownload> getGefilterteBezugsfaehigeDownloads() {
        LOGGER.trace("stichwortsuche.isStichwortHatTreffer()={}, {} Ergebnisse",
                stichwortsuche.isStichwortHatTreffer(), stichwortsuche.getGefiltert().size());
        return stichwortsuche.isStichwortHatTreffer()
                ? stichwortsuche.getGefiltert()
                : hoererSession.bezugsfaehigeDownloads();
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