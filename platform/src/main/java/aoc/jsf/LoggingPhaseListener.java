/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.jsf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * <pre>
 *     &lt;lifecycle&gt;
 *         &lt;phase-listener&gt;aoc.jsf.LoggingPhaseListener&lt;/phase-listener&gt;
 *     &lt;/lifecycle&gt;
 * </pre>
 */
public class LoggingPhaseListener implements PhaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPhaseListener.class);

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(final PhaseEvent event) {
        if (PhaseId.RESTORE_VIEW == event.getPhaseId()) {
            LOGGER.debug("Processing new request ({})", event.getPhaseId().getName());
        }
        LOGGER.debug("Before phase {}", event.getPhaseId().getName());
    }

    @Override
    public void afterPhase(final PhaseEvent event) {
        LOGGER.debug("After phase {}", event.getPhaseId().getName());
        if (PhaseId.RENDER_RESPONSE == event.getPhaseId()) {
            LOGGER.debug("Request processing done ({})", event.getPhaseId().getName());
        }
    }

}
