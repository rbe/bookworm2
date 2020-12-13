package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

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
        final String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), status, request);
    }

}
