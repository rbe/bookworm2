/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

import static wbh.bookworm.hoerbuchkatalog.ui.katalog.SessionKey.SCOPEDTARGET_HOERERSESSION;

@Configuration
public class AuthenticationProfile {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProfile.class);

    private static final List<String> TRUSTED_IP = Arrays.asList(
            "127.0.0.1",
            "172.16.",
            "192.168.");

    @Bean
    @Profile({"development", "test"})
    public Consumer<HttpServletRequest> hoerernummerHttpRequestDevelopment() {
        return new HoerernummerInSessionSetzen();
    }

    @Bean
    @Profile({"production"/*, "development"*//*, "test"*/})
    public Consumer<HttpServletRequest> hoerernummerHttpRequestProduction() {
        final Predicate<HttpServletRequest> a = request ->
                TRUSTED_IP.stream().anyMatch(t -> request.getRemoteAddr().contains(t));
        final Consumer<HttpServletRequest> b = new HoerernummerInSessionSetzen();
        return request -> {
            LOGGER.debug("Prüfe {}, remoteAddr={} remoteHost={}",
                    request, request.getRemoteAddr(), request.getRemoteHost());
            if (a.test(request)) {
                LOGGER.debug("Request von remoteAddr={} remoteHost={} akzeptiert",
                        request.getRemoteAddr(), request.getRemoteHost());
                b.accept(request);
            } else {
                LOGGER.error("Hörernummer ({}) kann nur von" +
                                " vertrauenswürdigen Systemen ({}) gesetzt werden," +
                                " nicht jedoch von {}",
                        request.getParameter(SessionKey.HOERERNUMMER),
                        TRUSTED_IP, request.getRemoteAddr());
            }
        };
    }

    private static class HoerernummerInSessionSetzen implements Consumer<HttpServletRequest> {

        private static final Logger LOGGER = LoggerFactory.getLogger(HoerernummerInSessionSetzen.class);

        private static final Predicate<String> isNotNullAndNotEmpty =
                str -> null != str && !str.isBlank();

        @Override
        public void accept(final HttpServletRequest request) {
            if (!request.getRequestURI().endsWith(".xhtml")) {
                return;
            }
            LOGGER.trace("Suche Hoerernummer in HTTP-Anfrage {}", request.getRequestURI());
            final String requestHnr = request.getParameter(SessionKey.HOERERNUMMER);
            if (isNotNullAndNotEmpty.test(requestHnr)) {
                final HttpSession session = request.getSession(false);
                if (null != session) {
                    LOGGER.trace("HttpSession ID {} ist {}, lastAccessedTime={}",
                            session.getId(), session.isNew() ? "neu" : "nicht neu",
                            new Date(session.getLastAccessedTime()));
                    final Hoerernummer sessionHnr =
                            (Hoerernummer) session.getAttribute(SessionKey.HOERERNUMMER);
                    final boolean hnrInSessionNichtGesetztOderUnbekannt =
                            null == sessionHnr || sessionHnr.isUnbekannt();
                    if (hnrInSessionNichtGesetztOderUnbekannt) {
                        hoerernummerInHoererSessionSetzen(session, requestHnr);
                    } else {
                        LOGGER.trace("Hörernummer {} in HttpSession {} bereits gesetzt",
                                sessionHnr, session.getId());
                    }
                } else {
                    LOGGER.debug("Keine HttpSession bei Abruf von {}", request.getRequestURI());
                }
            } else {
                LOGGER.trace("Keine Hörernummer in ParameterMap={} gefunden",
                        request.getParameterMap());
            }
        }

        private void hoerernummerInHoererSessionSetzen(final HttpSession session,
                                                       final String requestHnr) {
            final Optional<HoererSession> maybeScopedHoererSession = scopedHoererSession(session);
            if (maybeScopedHoererSession.isPresent()) {
                final HoererSession scopedHoererSession = maybeScopedHoererSession.get();
                // TODO Auf bekannte Hörernummer testen
                if (isNotNullAndNotEmpty.test(requestHnr)) {
                    final Hoerernummer hoerernummer = new Hoerernummer(requestHnr);
                    scopedHoererSession.hoererSetzen(hoerernummer);
                    LOGGER.debug("Hörernummer {} für HttpSession {} gesetzt",
                            requestHnr, session.getId());
                } else {
                    scopedHoererSession.hoererSetzen(Hoerernummer.UNBEKANNT);
                    LOGGER.debug("Unbekannter Hörer für HttpSession {} gesetzt",
                            session.getId());
                }
            }
        }

        private Optional<HoererSession> scopedHoererSession(final HttpSession session) {
            final HoererSession scopedHoererSession =
                    (HoererSession) session.getAttribute(SCOPEDTARGET_HOERERSESSION);
            if (null == scopedHoererSession) {
                LOGGER.trace("Bean {} nicht in der Session gefunden",
                        SCOPEDTARGET_HOERERSESSION);
                return Optional.empty();
            } else {
                return Optional.of(scopedHoererSession);
            }
        }

    }

}
