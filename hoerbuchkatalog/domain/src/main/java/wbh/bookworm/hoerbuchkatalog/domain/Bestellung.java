/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

public final class Bestellung extends DddEntity<Bestellung> {

    private String name;

    private Hoerernummer hoerernummer;

    private String email;

    private String bemerkung;

    private Boolean bestellkarteMischen;

    private Boolean alteBestellkarteLoeschen;

    public Bestellung(final String name, final Hoerernummer hoerernummer,
                      final String email, final String bemerkung,
                      final Boolean bestellkarteMischen, final Boolean alteBestellkarteLoeschen) {
        this.name = name;
        this.hoerernummer = hoerernummer;
        this.email = email;
        this.bemerkung = bemerkung;
        this.bestellkarteMischen = bestellkarteMischen;
        this.alteBestellkarteLoeschen = alteBestellkarteLoeschen;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public void setHoerernummer(final Hoerernummer hoerernummer) {
        this.hoerernummer = hoerernummer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(final String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Boolean getBestellkarteMischen() {
        return bestellkarteMischen;
    }

    public void setBestellkarteMischen(final Boolean bestellkarteMischen) {
        this.bestellkarteMischen = bestellkarteMischen;
    }

    public Boolean getAlteBestellkarteLoeschen() {
        return alteBestellkarteLoeschen;
    }

    public void setAlteBestellkarteLoeschen(final Boolean alteBestellkarteLoeschen) {
        this.alteBestellkarteLoeschen = alteBestellkarteLoeschen;
    }

    @Override
    public int compareTo(final Bestellung o) {
        return o.hoerernummer.compareTo(this.hoerernummer);
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
