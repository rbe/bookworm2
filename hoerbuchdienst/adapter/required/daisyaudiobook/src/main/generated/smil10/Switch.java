//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.03.11 um 08:12:53 AM CET 
//


package org.w3c.smil10;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;group ref="{}switch-content" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;attGroup ref="{}switch-attlist"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "switchContent"
})
public class Switch {

    @XmlElementRefs({
        @XmlElementRef(name = "layout", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "schedule", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "switch", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "link", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> switchContent;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "title")
    @XmlSchemaType(name = "anySimpleType")
    protected String title;

    /**
     * Gets the value of the switchContent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the switchContent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSwitchContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Layout }{@code >}
     * {@link JAXBElement }{@code <}{@link Audio }{@code >}
     * {@link JAXBElement }{@code <}{@link Ref }{@code >}
     * {@link JAXBElement }{@code <}{@link Video }{@code >}
     * {@link JAXBElement }{@code <}{@link Text }{@code >}
     * {@link JAXBElement }{@code <}{@link Textstream }{@code >}
     * {@link JAXBElement }{@code <}{@link Animation }{@code >}
     * {@link JAXBElement }{@code <}{@link Img }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Par }{@code >}
     * {@link JAXBElement }{@code <}{@link Seq }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Switch }{@code >}
     * {@link JAXBElement }{@code <}{@link Link }{@code >}
     * {@link JAXBElement }{@code <}{@link Link }{@code >}
     * {@link JAXBElement }{@code <}{@link Link }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getSwitchContent() {
        if (switchContent == null) {
            switchContent = new ArrayList<JAXBElement<?>>();
        }
        return this.switchContent;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

}
