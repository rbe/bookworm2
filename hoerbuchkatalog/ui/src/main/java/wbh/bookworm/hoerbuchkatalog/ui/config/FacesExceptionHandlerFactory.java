/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacesExceptionHandlerFactory extends ExceptionHandlerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacesExceptionHandlerFactory.class);

    private final ExceptionHandlerFactory wrapped;

    @SuppressWarnings({"deprecation"})
    public FacesExceptionHandlerFactory(final ExceptionHandlerFactory wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new CustomExceptionHandler(wrapped.getExceptionHandler());
    }

    public static class CustomExceptionHandler extends ExceptionHandlerWrapper {

        private ExceptionHandler exceptionHandler;

        @SuppressWarnings({"deprecation"})
        CustomExceptionHandler(ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public ExceptionHandler getWrapped() {
            return exceptionHandler;
        }

        @Override
        public void handle() throws FacesException {
            final Iterator<ExceptionQueuedEvent> queue = getUnhandledExceptionQueuedEvents().iterator();
            while (queue.hasNext()) {
                final ExceptionQueuedEvent item = queue.next();
                final ExceptionQueuedEventContext exceptionQueuedEventContext = (ExceptionQueuedEventContext) item.getSource();
                try {
                    final Throwable throwable = exceptionQueuedEventContext.getException();
                    LOGGER.error("", throwable);
                    final FacesContext context = FacesContext.getCurrentInstance();
                    final Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                    final NavigationHandler nav = context.getApplication().getNavigationHandler();
                    requestMap.put("error-message", throwable.getMessage());
                    requestMap.put("error-stack", throwable.getStackTrace());
                    nav.handleNavigation(context, null, "support");
                    context.renderResponse();
                } finally {
                    queue.remove();
                }
            }
        }

    }

    /*
    public static class M extends FullAjaxExceptionHandler {

        public M(final ExceptionHandler wrapped) {
            super(wrapped);
        }

        @Override
        protected String findErrorPageLocation(final FacesContext context, final Throwable exception) {
            return "/support.xhtml";
        }

    }
    */

}
