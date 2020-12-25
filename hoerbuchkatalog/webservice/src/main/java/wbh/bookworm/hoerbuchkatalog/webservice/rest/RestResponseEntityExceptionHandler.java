package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import aoc.mikrokosmos.ddd.model.DomainValueException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {DomainValueException.class})
    protected ResponseEntity<Object> handleDomainValueException(final Exception ex, final WebRequest request) {
        return getObjectResponseEntity(ex, request, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleRuntimeException(final Exception ex, final WebRequest request) {
        return getObjectResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleBadRequest(final Exception ex, final WebRequest request) {
        return getObjectResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(final Exception ex, final WebRequest request) {
        return getObjectResponseEntity(ex, request, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> getObjectResponseEntity(final Exception ex,
                                                           final WebRequest request,
                                                           final HttpStatus status) {
        LOGGER.error("", ex);
        final String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), status, request);
    }

}
