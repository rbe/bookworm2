//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.03.11 um 08:12:53 AM CET 
//


package org.w3c.smil10;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für mo-content complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="mo-content"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}assoc-link" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mo-content", propOrder = {
    "assocLink"
})
@XmlSeeAlso({
    Ref.class,
    Audio.class,
    Img.class,
    Video.class,
    Text.class,
    Textstream.class,
    Animation.class
})
public class MoContent {

    @XmlElementRef(name = "assoc-link", type = JAXBElement.class, required = false)
    protected List<JAXBElement<AssocLink>> assocLink;

    /**
     * Gets the value of the assocLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assocLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssocLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AssocLink }{@code >}
     * {@link JAXBElement }{@code <}{@link AssocLink }{@code >}
     * 
     * 
     */
    public List<JAXBElement<AssocLink>> getAssocLink() {
        if (assocLink == null) {
            assocLink = new ArrayList<JAXBElement<AssocLink>>();
        }
        return this.assocLink;
    }

}
