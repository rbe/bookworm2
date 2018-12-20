/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.jsf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

@WebServlet(urlPatterns = {"/css/*", "/js/*", "/img/*", "/image/*", "/fonts/*"})
public final class StaticResourceServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceServlet.class);

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) {
        final String requestedResource = getRequestedResourceFrom(request);
        final boolean requestHasRequestedResource = null != requestedResource;
        if (!requestHasRequestedResource) {
            sendResourceNotFound(response, requestedResource);
        } else {
            final String decodedResourceName =
                    URLDecoder.decode(requestedResource, StandardCharsets.UTF_8);
            final Path resource = Path.of(/* TODO WebFilesystem.BASE_PATH,*/ decodedResourceName);
            final boolean resourceUsable = Files.exists(resource) && Files.isReadable(resource);
            if (!resourceUsable) {
                sendResourceNotUsable(response, resource);
            } else {
                maybeSendRequestedResource(response, resource);
            }
        }
    }

    private void maybeSendRequestedResource(final HttpServletResponse response,
                                            final Path resource) {
        final String contentType =
                getServletContext().getMimeType(resource.getFileName().toString());
        if (!isContentTypeAllowed(contentType)) {
            sendContentTypeNotAllowed(response);
        } else {
            sendRequestedResource(response, resource, contentType);
        }
    }

    private void sendRequestedResource(final HttpServletResponse response,
                                       final Path resource, final String contentType) {
        LOGGER.trace("Sending requested resource '{}' of allowed mime type '{}'",
                resource.toAbsolutePath(), contentType);
        response.reset();
        response.setContentType(contentType);
        try {
            response.setHeader("Content-Length", String.valueOf(Files.size(resource)));
            Files.copy(resource, response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private static final String[] ALLOWED_CONTENT_TYPES = {"text", "image"};

    private boolean isContentTypeAllowed(final String contentType) {
        Objects.requireNonNull(contentType);
        return Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType);
    }

    private void sendContentTypeNotAllowed(final HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private void sendResourceNotFound(final HttpServletResponse response,
                                      final String requestedResource) {
        Objects.requireNonNull(response);
        Objects.requireNonNull(requestedResource);
        LOGGER.error("No requested resource: {}", requestedResource);
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private void sendResourceNotUsable(final HttpServletResponse response,
                                       final Path requestedResource) {
        Objects.requireNonNull(response);
        Objects.requireNonNull(requestedResource);
        LOGGER.error("Requested resource is not readable: {}", requestedResource);
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    private String getRequestedResourceFrom(final HttpServletRequest request) {
        Objects.requireNonNull(request);
        String requestedResource = request.getRequestURI();
        final String contextPath = request.getContextPath();
        if (null != contextPath && !contextPath.isBlank()) {
            requestedResource = requestedResource.substring(contextPath.length());
        }
        return requestedResource;
    }

}
