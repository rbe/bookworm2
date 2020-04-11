//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.03.11 um 08:12:53 AM CET 
//


package org.w3c.smil10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
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
 *     &lt;extension base="{}mo-content"&gt;
 *       &lt;attGroup ref="{}img-attlist"/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Img
    extends MoContent
{

    @XmlAttribute(name = "region")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object region;
    @XmlAttribute(name = "alt")
    @XmlSchemaType(name = "anySimpleType")
    protected String alt;
    @XmlAttribute(name = "longdesc")
    @XmlSchemaType(name = "anySimpleType")
    protected String longdesc;
    @XmlAttribute(name = "src")
    @XmlSchemaType(name = "anySimpleType")
    protected String src;
    @XmlAttribute(name = "type")
    @XmlSchemaType(name = "anySimpleType")
    protected String type;
    @XmlAttribute(name = "dur")
    @XmlSchemaType(name = "anySimpleType")
    protected String dur;
    @XmlAttribute(name = "repeat")
    @XmlSchemaType(name = "anySimpleType")
    protected String repeat;
    @XmlAttribute(name = "begin")
    @XmlSchemaType(name = "anySimpleType")
    protected String begin;
    @XmlAttribute(name = "end")
    @XmlSchemaType(name = "anySimpleType")
    protected String end;
    @XmlAttribute(name = "fill")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fill;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "abstract")
    @XmlSchemaType(name = "anySimpleType")
    protected String _abstract;
    @XmlAttribute(name = "author")
    @XmlSchemaType(name = "anySimpleType")
    protected String author;
    @XmlAttribute(name = "copyright")
    @XmlSchemaType(name = "anySimpleType")
    protected String copyright;
    @XmlAttribute(name = "title")
    @XmlSchemaType(name = "anySimpleType")
    protected String title;
    @XmlAttribute(name = "system-bitrate")
    @XmlSchemaType(name = "anySimpleType")
    protected String systemBitrate;
    @XmlAttribute(name = "system-language")
    @XmlSchemaType(name = "anySimpleType")
    protected String systemLanguage;
    @XmlAttribute(name = "system-required")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String systemRequired;
    @XmlAttribute(name = "system-screen-size")
    @XmlSchemaType(name = "anySimpleType")
    protected String systemScreenSize;
    @XmlAttribute(name = "system-screen-depth")
    @XmlSchemaType(name = "anySimpleType")
    protected String systemScreenDepth;
    @XmlAttribute(name = "system-captions")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String systemCaptions;
    @XmlAttribute(name = "system-overdub-or-caption")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String systemOverdubOrCaption;

    /**
     * Ruft den Wert der region-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRegion() {
        return region;
    }

    /**
     * Legt den Wert der region-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRegion(Object value) {
        this.region = value;
    }

    /**
     * Ruft den Wert der alt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlt() {
        return alt;
    }

    /**
     * Legt den Wert der alt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlt(String value) {
        this.alt = value;
    }

    /**
     * Ruft den Wert der longdesc-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongdesc() {
        return longdesc;
    }

    /**
     * Legt den Wert der longdesc-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongdesc(String value) {
        this.longdesc = value;
    }

    /**
     * Ruft den Wert der src-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrc() {
        return src;
    }

    /**
     * Legt den Wert der src-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrc(String value) {
        this.src = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der dur-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDur() {
        return dur;
    }

    /**
     * Legt den Wert der dur-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDur(String value) {
        this.dur = value;
    }

    /**
     * Ruft den Wert der repeat-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepeat() {
        if (repeat == null) {
            return "1";
        } else {
            return repeat;
        }
    }

    /**
     * Legt den Wert der repeat-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepeat(String value) {
        this.repeat = value;
    }

    /**
     * Ruft den Wert der begin-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBegin() {
        return begin;
    }

    /**
     * Legt den Wert der begin-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBegin(String value) {
        this.begin = value;
    }

    /**
     * Ruft den Wert der end-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnd() {
        return end;
    }

    /**
     * Legt den Wert der end-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnd(String value) {
        this.end = value;
    }

    /**
     * Ruft den Wert der fill-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFill() {
        if (fill == null) {
            return "remove";
        } else {
            return fill;
        }
    }

    /**
     * Legt den Wert der fill-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFill(String value) {
        this.fill = value;
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
     * Ruft den Wert der abstract-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Legt den Wert der abstract-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Ruft den Wert der author-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Legt den Wert der author-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Ruft den Wert der copyright-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Legt den Wert der copyright-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyright(String value) {
        this.copyright = value;
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

    /**
     * Ruft den Wert der systemBitrate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemBitrate() {
        return systemBitrate;
    }

    /**
     * Legt den Wert der systemBitrate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemBitrate(String value) {
        this.systemBitrate = value;
    }

    /**
     * Ruft den Wert der systemLanguage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemLanguage() {
        return systemLanguage;
    }

    /**
     * Legt den Wert der systemLanguage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemLanguage(String value) {
        this.systemLanguage = value;
    }

    /**
     * Ruft den Wert der systemRequired-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemRequired() {
        return systemRequired;
    }

    /**
     * Legt den Wert der systemRequired-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemRequired(String value) {
        this.systemRequired = value;
    }

    /**
     * Ruft den Wert der systemScreenSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemScreenSize() {
        return systemScreenSize;
    }

    /**
     * Legt den Wert der systemScreenSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemScreenSize(String value) {
        this.systemScreenSize = value;
    }

    /**
     * Ruft den Wert der systemScreenDepth-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemScreenDepth() {
        return systemScreenDepth;
    }

    /**
     * Legt den Wert der systemScreenDepth-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemScreenDepth(String value) {
        this.systemScreenDepth = value;
    }

    /**
     * Ruft den Wert der systemCaptions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemCaptions() {
        return systemCaptions;
    }

    /**
     * Legt den Wert der systemCaptions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemCaptions(String value) {
        this.systemCaptions = value;
    }

    /**
     * Ruft den Wert der systemOverdubOrCaption-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemOverdubOrCaption() {
        return systemOverdubOrCaption;
    }

    /**
     * Legt den Wert der systemOverdubOrCaption-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemOverdubOrCaption(String value) {
        this.systemOverdubOrCaption = value;
    }

}
