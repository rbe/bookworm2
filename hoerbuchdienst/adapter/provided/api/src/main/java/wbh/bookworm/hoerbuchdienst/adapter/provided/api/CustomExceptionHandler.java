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
import lombok.extern.slf4j.Slf4j;

@Singleton
@Produces
@Requires(classes = {BusinessException.class, ExceptionHandler.class})
@Slf4j
public class CustomExceptionHandler implements ExceptionHandler<BusinessException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, BusinessException ex) {
        log.error("", ex);
        final ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage(), "");
        return HttpResponse.status(HttpStatus.CONFLICT).body(apiError);
    }

}
