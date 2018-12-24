/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ViewResource;
import javax.faces.context.FacesContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public final class ExternalizedFaceletsResourceHandler extends ResourceHandlerWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalizedFaceletsResourceHandler.class);

    private ResourceHandler wrapped;

    @SuppressWarnings({"deprecation"})
    public ExternalizedFaceletsResourceHandler(final ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ViewResource createViewResource(final FacesContext context, final String name) {
        LOGGER.trace("Resolving name={} basePath={} context={}",
                name, ExternalizedFilesystem.TEMPLATES_PATH, context);
        ViewResource resource = super.createViewResource(context, name);
        if (null == resource) {
            resource = new ViewResource() {
                @Override
                public URL getURL() {
                    try {
                        final Path resolve = ExternalizedFilesystem.TEMPLATES_PATH.resolve(name.substring(1));
                        return resolve.toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new FacesException(e);
                    }
                }
            };
        }
        LOGGER.debug("Resolved name={} basePath={} context={} = {}",
                name, ExternalizedFilesystem.TEMPLATES_PATH, context, resource.getURL());
        return resource;
    }

    private static final String[] libraryNames = {"hoerbuchkatalog", "katalogsuche", "warenkorb"};

    @Override
    public Resource createResource(final String resourceName, final String libraryName) {
        Objects.requireNonNull(resourceName);
        Objects.requireNonNull(libraryName);
        final boolean isResponsible = !libraryName.isBlank()
                && Arrays.asList(libraryNames).contains(libraryName);
        if (isResponsible) {
            return new ExternalizedFaceletsResource(resourceName, libraryName, "text/html");
        } else {
            return super.createResource(resourceName, libraryName);
        }
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

}
