/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.faces.event.PhaseEvent;

@Component
@SessionScope
public class AjaxTest {

    private String message;

    public String getMessage() {
        return message;
    }

    public void sayHello() {
        message = "Hello!!";
    }

    public void sayNothing() {
        message = "";
    }

    public void phaseListener(PhaseEvent e){
        System.out.println(e);
    }

}
