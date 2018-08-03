/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.Titelnummer;

import java.time.format.DateTimeFormatter;

@Component
@SessionScope
public class Hoerbuchdetail {

    private Hoerbuch hoerbuch;

    public void setHoerbuch(final Hoerbuch hoerbuch) {
        this.hoerbuch = hoerbuch;
    }

    public Hoerbuch getHoerbuch() {
        return hoerbuch;
    }

    public Sachgebiet getSachgebiet() {
        return hoerbuch.getSachgebiet();
    }

    public Titelnummer getTitelnummer() {
        return hoerbuch.getTitelnummer();
    }

    public String getAutor() {
        return hoerbuch.getAutor();
    }

    public String getTitel() {
        return hoerbuch.getTitel();
    }

    public String getUntertitel() {
        return hoerbuch.getUntertitel();
    }

    public String getErlaeuterung() {
        return hoerbuch.getErlaeuterung();
    }

    public String getVerlagsort() {
        return hoerbuch.getVerlagsort();
    }

    public String getVerlag() {
        return hoerbuch.getVerlag();
    }

    public String getDruckjahr() {
        return hoerbuch.getDruckjahr();
    }

    public String getSprecher() {
        final StringBuilder builder = new StringBuilder();
        final String sprecher1 = hoerbuch.getSprecher1();
        final boolean sprecher1HatWert = null != sprecher1 && !sprecher1.trim().isEmpty();
        if (sprecher1HatWert) {
            builder.append(sprecher1);
        }
        final String sprecher2 = hoerbuch.getSprecher1();
        final boolean sprecher2HatWert = null != sprecher2 && !sprecher2.trim().isEmpty();
        if (sprecher2HatWert) {
            if (sprecher1HatWert) {
                builder.append(", ");
            }
            builder.append(sprecher2);
        }
        return sprecher1;
    }

    public String getSpieldauer() {
        return hoerbuch.getSpieldauer();
    }

    public String getProdOrt() {
        return hoerbuch.getProdOrt();
    }

    public String getProdJahr() {
        return hoerbuch.getProdJahr();
    }

    public String getAnzahlCD() {
        return hoerbuch.getAnzahlCD();
    }

    public String getTitelfamilie() {
        return hoerbuch.getTitelfamilie();
    }

    public String getEinstelldatum() {
        return hoerbuch.getEinstelldatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}
