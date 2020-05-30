/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import javax.faces.context.FacesContext;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class Support {

    private Map<String, Object> getRequestMap() {
        return FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestMap();
    }

    private String getString(final String s) {
        return (String) getRequestMap().get(s);
    }

    public String getStatusCode() {
        return getString("javax.servlet.error.status_code");
    }

    public String getMessage() {
        return getString("javax.servlet.error.message");
    }

    public String getExceptionType() {
        return getString("javax.servlet.error.exception_type");
    }

    public String getException() {
        return getString("javax.servlet.error.exception");
    }

    public String getRequestURI() {
        return getString("javax.servlet.error.request_uri");
    }

    public String getServletName() {
        return getString("javax.servlet.error.servlet_name");
    }

}
