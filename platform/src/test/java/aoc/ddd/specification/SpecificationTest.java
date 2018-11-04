/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.specification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecificationTest {

    @Test
    void shouldGuterKundeAusHamburg() {
        final Specification<Kunde> guterKundeAusHamburg =
                new IstAusHamburg().and(new IstGuterKunde());
        assertTrue(guterKundeAusHamburg.isSatisfied(new KundeAusHamburg()));
    }

    @Test
    void shouldGuterKundeAusMuenster() {
        final Specification<Kunde> guterKundeNichtAusHamburg =
                new IstGuterKunde().andNot(new IstAusHamburg());
        assertTrue(guterKundeNichtAusHamburg.isSatisfied(new KundeAusMuenster()));
    }

    private static class IstGuterKunde implements Specification<Kunde> {

        @Override
        public boolean isSatisfied(final Kunde candidate) {
            return candidate.umsatz > 3000;
        }

    }

    private static class IstAusHamburg implements Specification<Kunde> {

        @Override
        public boolean isSatisfied(final Kunde candidate) {
            return candidate.plz.startsWith("21");
        }

    }

    private static class Kunde {

        protected int umsatz;

        protected String plz;

    }

    private static class KundeAusHamburg extends Kunde {

        {
            umsatz = 3500;
            plz = "21075";
        }

    }

    private static class KundeAusMuenster extends Kunde {

        {
            umsatz = 3500;
            plz = "48159";
        }

    }

}
