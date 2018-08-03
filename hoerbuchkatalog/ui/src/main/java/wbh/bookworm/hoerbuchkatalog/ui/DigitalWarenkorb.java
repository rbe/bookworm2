/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class DigitalWarenkorb extends AbstractWarenkorb {

    public boolean isMaxDownloadsProTagErreicht() {
        return false;
    }

    public boolean isMaxDownloadsProMonatErreicht() {
        return false;
    }

    public boolean isMaxDownloadsErreicht() {
        return isMaxDownloadsProTagErreicht() || isMaxDownloadsProMonatErreicht();
    }

}
