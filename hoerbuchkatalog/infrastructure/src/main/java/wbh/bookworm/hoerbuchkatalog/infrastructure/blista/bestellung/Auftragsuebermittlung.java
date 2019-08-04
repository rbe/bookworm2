/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import aoc.strings.RandomStringGenerator;

import com.jcraft.jsch.ChannelSftp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
class Auftragsuebermittlung {

    private static final Logger LOGGER = LoggerFactory.getLogger(Auftragsuebermittlung.class);

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

        Billet() {
        }

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
            return String.format("Billet{userId='%s', bestellnummer='%s'}", userId, bestellnummer);
        }

    }

    @Autowired
    Auftragsuebermittlung(final SftpClient sftpClient) {
        this.sftpClient = sftpClient;
    }

    /**
     * @param userId ID of user requesting a download.
     * @param bestellnummer Bestellnummer == AGH-Nummer.
     * @return {@link Billet}.
     */
    private static Billet auftragErstellen(final String userId, final String bestellnummer) {
        return new Billet(userId, bestellnummer, RandomStringGenerator.next());
    }

    private static String marshal(final Billet billet) {
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
     * Bestellung an blista DLS übergeben.
     *
     * @param userId UserId/Hörernummer.
     * @param aghNummer Bestellnummer/AGH Nummer.
     */
    boolean uebergeben(final String userId, final String aghNummer) {
        LOGGER.trace("Übergebe Bestellung für {} / {} per SFTP", userId, aghNummer);
        final Billet billet = auftragErstellen(userId, aghNummer);
        try {
            sftpClient.with(delegate -> {
                delegate.cd("new");
                final String billetXml = marshal(billet);
                final String filename = String.format("%s-%s.blt", billet.getUserId(), billet.getAghNummer());
                delegate.putOverwrite(billetXml, filename);
            });
            LOGGER.debug("Bestellung für {} / {} per SFTP übergeben: {}",
                    userId, aghNummer, billet);
            return true;
        } catch (SftpClientException e) {
            LOGGER.error(String.format(
                    "Konnte Bestellung für %s / %s nicht per SFTP übergeben: %s",
                    userId, aghNummer, billet),
                    e);
            return false;
        }
    }

    Auftragsstatus auftragsstatus(final String userId, final String aghNummer) {
        final boolean rejected = inSftpOrdnerNachsehen("rejected", userId, aghNummer);
        if (rejected) {
            return Auftragsstatus.ABGELEHNT;
        }
        final boolean processed = inSftpOrdnerNachsehen("processed", userId, aghNummer);
        if (processed) {
            return Auftragsstatus.VERARBEITET;
        }
        return Auftragsstatus.UNBEKANNT;
    }

    private boolean inSftpOrdnerNachsehen(final String ordner,
                                          final String userId, final String aghNummer) {
        return sftpClient.with(delegate -> delegate.ls(ordner, entry -> {
            LOGGER.trace("Versuche Billet {} / {} im SFTP-Ordner '{}' zu finden",
                    userId, aghNummer, ordner);
            final boolean hasUserId = entry.getFilename().contains(userId);
            final boolean hasBestellnumer = entry.getFilename().contains(aghNummer);
            if (hasUserId && hasBestellnumer) {
                delegate.result = true;
                LOGGER.debug("Billet {} / {} im SFTP-Ordner '{}' gefunden",
                        userId, aghNummer, ordner);
                return ChannelSftp.LsEntrySelector.BREAK;
            }
            return ChannelSftp.LsEntrySelector.CONTINUE;
        }));
    }

}
