/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;
import wbh.bookworm.platform.ddd.model.DomainAggregate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Bestellung extends DomainAggregate<Bestellung> {

    private static final long serialVersionUID = -1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Bestellung.class);

    private Hoerernummer hoerernummer;

    private String hoerername;

    private String hoereremail;

    private String bemerkung;

    private Boolean bestellkarteMischen;

    private Boolean alteBestellkarteLoeschen;

    private final CdWarenkorb cdWarenkorb;
    private final DownloadWarenkorb downloadWarenkorb;

    public Bestellung(final String hoerername, final Hoerernummer hoerernummer,
                      final String hoereremail, final String bemerkung,
                      final Boolean bestellkarteMischen, final Boolean alteBestellkarteLoeschen,
                      final CdWarenkorb cdWarenkorb, final DownloadWarenkorb downloadWarenkorb) {
        this.hoerername = hoerername;
        this.hoerernummer = hoerernummer;
        this.hoereremail = hoereremail;
        this.bemerkung = bemerkung;
        this.bestellkarteMischen = bestellkarteMischen;
        this.alteBestellkarteLoeschen = alteBestellkarteLoeschen;
        this.cdWarenkorb = cdWarenkorb;
        this.downloadWarenkorb = downloadWarenkorb;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public void setHoerernummer(final Hoerernummer hoerernummer) {
        this.hoerernummer = hoerernummer;
    }

    public String getHoerername() {
        return hoerername;
    }

    public String getHoereremail() {
        return hoereremail;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public Boolean getBestellkarteMischen() {
        return bestellkarteMischen;
    }

    public Boolean getAlteBestellkarteLoeschen() {
        return alteBestellkarteLoeschen;
    }

    /**
     * TODO Command
     */
    public void abschicken() {
        LOGGER.trace("Bestellung {} für {} wird abgeschickt!", this, hoerernummer);
        DomainEventPublisher.global()
                .publish(new BestellungAbgeschickt(hoerernummer,
                        cdWarenkorb.getTitelnummern(), downloadWarenkorb.getTitelnummern()));
        LOGGER.info("Bestellung {} für {} wurde abgeschickt!", this, hoerernummer);
    }

    @Override
    public int compareTo(final Bestellung o) {
        /* TODO Comparable */return o.hoerernummer.compareTo(this.hoerernummer);
    }

    @Override
    public String toString() {
        return String.format("Bestellung{" +
                        "hoerernummer=%s," +
                        " bemerkung='%s'," +
                        " bestellkarteMischen=%s," +
                        " alteBestellkarteLoeschen=%s" +
                        "}",
                hoerernummer, bemerkung, bestellkarteMischen, alteBestellkarteLoeschen);
    }

}
