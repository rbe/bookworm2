/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import javax.servlet.http.HttpServletRequest;

//@Controller
public class MyErrorController /*implements ErrorController*/ {

    //@RequestMapping("/error")
    public String handleError(final HttpServletRequest request) {
        /*final Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (null != status) {
            final int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";
            }
        }*/
        return "error";
    }

    //@Override
    public String getErrorPath() {
        return "error";
    }

}
