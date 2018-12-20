/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import org.omnifaces.resourcehandler.DynamicResource;

import javax.faces.FacesException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExternalizedFaceletsResource extends DynamicResource {

    public ExternalizedFaceletsResource(final String resourceName,
                                        final String libraryName,
                                        final String contentType) {
        super(resourceName, libraryName, contentType);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(ExternalizedFilesystem.TEMPLATES_PATH.resolve(
                Path.of(getLibraryName(), getResourceName())));
    }

    @Override
    public URL getURL() {
        try {
            final Path libraryPath = Path.of(getLibraryName(), getResourceName());
            return ExternalizedFilesystem.TEMPLATES_PATH
                    .resolve(libraryPath).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new FacesException(String.format("Cannot resolve externalized facelet %s/%s",
                    getLibraryName(), getResourceName()), e);
        }
    }

/*
    @Override
    public Map<String, String> getResponseHeaders() {
        final Map<String, String> responseHeaders = new HashMap<>(4);
        responseHeaders.put("Last-Modified", formatRFC1123(new Date(getLastModified())));
        responseHeaders.put("Expires", formatRFC1123(new Date(System.currentTimeMillis() + getDefaultResourceMaxAgeInMillis())));
        responseHeaders.put("Etag", String.format("W/\"%d-%d\"", getResourceName().hashCode(), getLastModified()));
        responseHeaders.put("Pragma", ""); // Explicitly set empty pragma to prevent some containers from setting it.
        return responseHeaders;
    }

    private long getLastModified() {
        return 0L;
    }

    @Override
    public String getRequestPath() {
        return null;
    }

    @Override
    public boolean userAgentNeedsUpdate(final FacesContext context) {
        return false;
    }

    private static final String PATTERN_RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone("GMT");
    private static String formatRFC1123(final Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_RFC1123_DATE, Locale.US);
        sdf.setTimeZone(TIMEZONE_GMT);
        return sdf.format(date);
    }

    private static volatile Long defaultResourceMaxAgeInMillis;
    private static final String[] PARAM_NAMES_RESOURCE_MAX_AGE = {
            */
    /* Mojarra *//*
"com.sun.faces.defaultResourceMaxAge",
            */
    /* MyFaces *//*
"org.apache.myfaces.RESOURCE_MAX_TIME_EXPIRES"
    };
    private static final long DEFAULT_RESOURCE_MAX_AGE = 604_800_000L; // 1 week.
    private static final String ERROR_MAX_AGE = "The '%s' init param must be a number. Encountered an invalid value of '%s'.";
    private static long getDefaultResourceMaxAgeInMillis() {
        if (null == defaultResourceMaxAgeInMillis) {
            Long resourceMaxAge = DEFAULT_RESOURCE_MAX_AGE;
            FacesContext context = FacesContext.getCurrentInstance();
            if (context == null) {
                return resourceMaxAge;
            }
            for (String name : PARAM_NAMES_RESOURCE_MAX_AGE) {
                String value = getInitParameter(context, name);
                if (value != null) {
                    try {
                        resourceMaxAge = Long.valueOf(value);
                        break;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(String.format(ERROR_MAX_AGE, name, value), e);
                    }
                }
            }
            defaultResourceMaxAgeInMillis = resourceMaxAge;
        }
        return defaultResourceMaxAgeInMillis;
    }
*/

}
