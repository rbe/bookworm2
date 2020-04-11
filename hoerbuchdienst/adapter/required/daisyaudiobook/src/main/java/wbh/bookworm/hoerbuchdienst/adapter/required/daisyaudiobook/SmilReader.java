/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import org.w3c.smil10.Smil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;

class SmilReader {

    private final JAXBContext jaxbContext;

    private final SAXParserFactory saxParserFactory;

    SmilReader() {
        try {
            jaxbContext = JAXBContext.newInstance("org.w3c.smil10");
        } catch (JAXBException e) {
            throw new SmilReaderException(e);
        }
        saxParserFactory = SAXParserFactory.newInstance();
        try {
            saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);
            saxParserFactory.setFeature("http://xml.org/sax/features/validation",
                    false);
        } catch (ParserConfigurationException | SAXNotSupportedException | SAXNotRecognizedException e) {
            throw new SmilReaderException(e);
        }
    }

    Smil from(final InputStream stream) {
        final InputSource inputSource = new InputSource(stream);
        final XMLReader xmlReader;
        try {
            xmlReader = saxParserFactory.newSAXParser().getXMLReader();
        } catch (SAXException | ParserConfigurationException e) {
            throw new SmilReaderException(e);
        }
        final Source source = new SAXSource(xmlReader, inputSource);
        final Unmarshaller unmarshaller;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
            return (Smil) unmarshaller.unmarshal(source);
        } catch (JAXBException e) {
            throw new SmilReaderException(e);
        }
    }

}
