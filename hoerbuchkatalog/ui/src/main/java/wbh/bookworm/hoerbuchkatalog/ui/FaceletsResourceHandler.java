/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.FacesException;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ViewResource;
import javax.faces.context.FacesContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public final class FaceletsResourceHandler extends ResourceHandlerWrapper {

    private ResourceHandler wrapped;

    @SuppressWarnings({"deprecation"})
    public FaceletsResourceHandler(final ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ViewResource createViewResource(FacesContext context, final String name) {
        ViewResource resource = super.createViewResource(context, name);
        if (resource == null) {
            resource = new ViewResource() {
                @Override
                public URL getURL() {
                    try {
                        return Paths.get(WebFilesystem.BASE_PATH, name).toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new FacesException(e);
                    }
                }
            };
        }
        return resource;
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

    private static final class WebFilesystem {

        private static final Logger LOGGER = LoggerFactory.getLogger(WebFilesystem.class);

        private static final String TEMPLATE_SYSTEM_VARIABLE = "HOERBUCHKATALOG_TEMPLATE";

        static final String BASE_PATH;

        static {
            BASE_PATH = System.getenv(TEMPLATE_SYSTEM_VARIABLE);
            if (null == WebFilesystem.BASE_PATH) {
                LOGGER.error("Variable {} not set!", TEMPLATE_SYSTEM_VARIABLE);
            }
        }

        private WebFilesystem() {
            throw new AssertionError();
        }

    }

}
