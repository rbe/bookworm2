/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Objects;

@Component
class Auftragsuebermittlung {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Billet.class);
        } catch (JAXBException e) {
            throw new DlsBestellungException(e);
        }
    }

    private final SftpClient sftpClient;

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static final class Billet implements Serializable {

        private static final long serialVersionUID = -1L;

        @XmlElement(name = "UserID")
        private String userId;

        @XmlElement(name = "BibliothekID")
        private String bibliothekId = "wbh06";

        @XmlElement(name = "Bestellnummer")
        private String bestellnummer;

        @XmlElement(name = "Abrufkennwort")
        private String abrufkennwort;

        /**
         * @param userId ID of user requesting a download.
         * @param bestellnummer Bestellnummer == AGH-Nummer.
         * @return {@link Billet}.
         */
        static Billet erstellen(final String userId, final String bestellnummer) {
            return new Billet(userId, bestellnummer, RandomStringGenerator.next());
        }

        Billet() {}

        private Billet(final String userId, final String bestellnummer, final String abrufkennwort) {
            this.userId = userId;
            this.bestellnummer = bestellnummer;
            this.abrufkennwort = abrufkennwort;
        }

        String getUserId() {
            return userId;
        }

        String getAghNummer() {
            return bestellnummer;
        }

        String getAbrufkennwort() {
            return abrufkennwort;
        }

        @Override
        public String toString() {
            return String.format("Billet{userId='%s', bestellnummer='%s', abrufkennwort='%s'}", userId, bestellnummer, abrufkennwort);
        }

    }

    @Autowired
    Auftragsuebermittlung(final SftpClient sftpClient) {
        this.sftpClient = sftpClient;
    }

    String marshal(final Billet billet) {
        try {
            final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            final StringWriter writer = new StringWriter();
            marshaller.marshal(billet, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new DlsBestellungException(e);
        }
    }

    /**
     * Place an order at blista DLS.
     * @param userId UserId/Hörernummer.
     * @param aghNummer Bestellnummer/AGH Nummer.
     * @return Abrufkennwort.
     */
    String uebergeben(final String userId, final String aghNummer) {
        final Billet billet = Billet.erstellen(userId, aghNummer);
        sftpClient.with(delegate -> {
            delegate.cd("new");
            final String billetXml = marshal(billet);
            final String filename = String.format("%s-%s.blt", billet.getUserId(), billet.getAghNummer());
            delegate.putOverwrite(billetXml, filename);
        });
        return billet.getAbrufkennwort();
    }

    Auftragsstatus auftragsstatus(final String userId, final String aghNummer) {
        final Auftragsstatus rejected = sftpClient.with(
                delegate -> delegate.ls("rejected", entry -> {
                    final boolean hasUserId = entry.getFilename().contains(userId);
                    final boolean hasBestellnumer = entry.getFilename().contains(aghNummer);
                    if (hasUserId && hasBestellnumer) {
                        delegate.result = Auftragsstatus.ABGELEHNT;
                        return ChannelSftp.LsEntrySelector.BREAK;
                    }
                    return ChannelSftp.LsEntrySelector.CONTINUE;
                }));
        if (null != rejected) {
            return rejected;
        } else {
            final Auftragsstatus processed = sftpClient.with(
                    delegate -> delegate.ls("processed", entry -> {
                        final boolean hasUserId = entry.getFilename().contains(userId);
                        final boolean hasBestellnumer = entry.getFilename().contains(aghNummer);
                        if (hasUserId && hasBestellnumer) {
                            delegate.result = Auftragsstatus.VERARBEITET;
                            return ChannelSftp.LsEntrySelector.BREAK;
                        }
                        return ChannelSftp.LsEntrySelector.CONTINUE;
                    }));
            return Objects.requireNonNullElse(processed, Auftragsstatus.UNBEKANNT);
        }
    }

}
