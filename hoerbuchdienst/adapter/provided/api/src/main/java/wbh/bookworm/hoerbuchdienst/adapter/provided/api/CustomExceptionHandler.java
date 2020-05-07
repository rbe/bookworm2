/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.api;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Produces
@Requires(classes = {BusinessException.class, ExceptionHandler.class})
public class CustomExceptionHandler implements ExceptionHandler<BusinessException, HttpResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Override
    public HttpResponse handle(final HttpRequest request, final BusinessException businessException) {
        LOGGER.error("", businessException);
        final ApiError apiError = new ApiError(HttpStatus.CONFLICT, businessException.getMessage(), "");
        return HttpResponse.status(HttpStatus.CONFLICT).body(apiError);
    }

}
