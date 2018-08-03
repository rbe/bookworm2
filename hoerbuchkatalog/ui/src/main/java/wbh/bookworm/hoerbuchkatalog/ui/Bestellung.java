/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class Bestellung {

    private final CdWarenkorb cdWarenkorb;

    private final DigitalWarenkorb digitalWarenkorb;

    @Autowired
    public Bestellung(final CdWarenkorb cdWarenkorb, final DigitalWarenkorb digitalWarenkorb) {
        this.cdWarenkorb = cdWarenkorb;
        this.digitalWarenkorb = digitalWarenkorb;
    }

    public int getAnzahl() {
        return cdWarenkorb.getAnzahl() + digitalWarenkorb.getAnzahl();
    }

}
