//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.03.11 um 08:12:53 AM CET 
//


package org.w3c.smil10;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.w3c.smil10 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LayoutSection_QNAME = new QName("", "layout-section");
    private final static QName _Schedule_QNAME = new QName("", "schedule");
    private final static QName _Switch_QNAME = new QName("", "switch");
    private final static QName _Link_QNAME = new QName("", "link");
    private final static QName _Layout_QNAME = new QName("", "layout");
    private final static QName _MediaObject_QNAME = new QName("", "media-object");
    private final static QName _InlineLink_QNAME = new QName("", "inline-link");
    private final static QName _AssocLink_QNAME = new QName("", "assoc-link");
    private final static QName _Par_QNAME = new QName("", "par");
    private final static QName _Seq_QNAME = new QName("", "seq");
    private final static QName _Ref_QNAME = new QName("", "ref");
    private final static QName _Audio_QNAME = new QName("", "audio");
    private final static QName _Img_QNAME = new QName("", "img");
    private final static QName _Video_QNAME = new QName("", "video");
    private final static QName _Text_QNAME = new QName("", "text");
    private final static QName _Textstream_QNAME = new QName("", "textstream");
    private final static QName _Animation_QNAME = new QName("", "animation");
    private final static QName _A_QNAME = new QName("", "a");
    private final static QName _Anchor_QNAME = new QName("", "anchor");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.w3c.smil10
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Smil }
     * 
     */
    public Smil createSmil() {
        return new Smil();
    }

    /**
     * Create an instance of {@link Head }
     * 
     */
    public Head createHead() {
        return new Head();
    }

    /**
     * Create an instance of {@link HeadElement }
     * 
     */
    public HeadElement createHeadElement() {
        return new HeadElement();
    }

    /**
     * Create an instance of {@link Meta }
     * 
     */
    public Meta createMeta() {
        return new Meta();
    }

    /**
     * Create an instance of {@link Body }
     * 
     */
    public Body createBody() {
        return new Body();
    }

    /**
     * Create an instance of {@link Switch }
     * 
     */
    public Switch createSwitch() {
        return new Switch();
    }

    /**
     * Create an instance of {@link Link }
     * 
     */
    public Link createLink() {
        return new Link();
    }

    /**
     * Create an instance of {@link Layout }
     * 
     */
    public Layout createLayout() {
        return new Layout();
    }

    /**
     * Create an instance of {@link Region }
     * 
     */
    public Region createRegion() {
        return new Region();
    }

    /**
     * Create an instance of {@link RootLayout }
     * 
     */
    public RootLayout createRootLayout() {
        return new RootLayout();
    }

    /**
     * Create an instance of {@link AssocLink }
     * 
     */
    public AssocLink createAssocLink() {
        return new AssocLink();
    }

    /**
     * Create an instance of {@link Par }
     * 
     */
    public Par createPar() {
        return new Par();
    }

    /**
     * Create an instance of {@link Seq }
     * 
     */
    public Seq createSeq() {
        return new Seq();
    }

    /**
     * Create an instance of {@link Ref }
     * 
     */
    public Ref createRef() {
        return new Ref();
    }

    /**
     * Create an instance of {@link Audio }
     * 
     */
    public Audio createAudio() {
        return new Audio();
    }

    /**
     * Create an instance of {@link Img }
     * 
     */
    public Img createImg() {
        return new Img();
    }

    /**
     * Create an instance of {@link Video }
     * 
     */
    public Video createVideo() {
        return new Video();
    }

    /**
     * Create an instance of {@link Text }
     * 
     */
    public Text createText() {
        return new Text();
    }

    /**
     * Create an instance of {@link Textstream }
     * 
     */
    public Textstream createTextstream() {
        return new Textstream();
    }

    /**
     * Create an instance of {@link Animation }
     * 
     */
    public Animation createAnimation() {
        return new Animation();
    }

    /**
     * Create an instance of {@link MoContent }
     * 
     */
    public MoContent createMoContent() {
        return new MoContent();
    }

    /**
     * Create an instance of {@link Any }
     * 
     */
    public Any createAny() {
        return new Any();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "layout-section")
    public JAXBElement<Object> createLayoutSection(Object value) {
        return new JAXBElement<Object>(_LayoutSection_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "schedule")
    public JAXBElement<Object> createSchedule(Object value) {
        return new JAXBElement<Object>(_Schedule_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Switch }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Switch }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "switch", substitutionHeadNamespace = "", substitutionHeadName = "layout-section")
    public JAXBElement<Switch> createSwitch(Switch value) {
        return new JAXBElement<Switch>(_Switch_QNAME, Switch.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Link }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "link")
    public JAXBElement<Link> createLink(Link value) {
        return new JAXBElement<Link>(_Link_QNAME, Link.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Layout }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Layout }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "layout", substitutionHeadNamespace = "", substitutionHeadName = "layout-section")
    public JAXBElement<Layout> createLayout(Layout value) {
        return new JAXBElement<Layout>(_Layout_QNAME, Layout.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "media-object", substitutionHeadNamespace = "", substitutionHeadName = "schedule")
    public JAXBElement<Object> createMediaObject(Object value) {
        return new JAXBElement<Object>(_MediaObject_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Link }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "inline-link", substitutionHeadNamespace = "", substitutionHeadName = "link")
    public JAXBElement<Link> createInlineLink(Link value) {
        return new JAXBElement<Link>(_InlineLink_QNAME, Link.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssocLink }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AssocLink }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "assoc-link")
    public JAXBElement<AssocLink> createAssocLink(AssocLink value) {
        return new JAXBElement<AssocLink>(_AssocLink_QNAME, AssocLink.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Par }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Par }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "par", substitutionHeadNamespace = "", substitutionHeadName = "schedule")
    public JAXBElement<Par> createPar(Par value) {
        return new JAXBElement<Par>(_Par_QNAME, Par.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Seq }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Seq }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "seq", substitutionHeadNamespace = "", substitutionHeadName = "schedule")
    public JAXBElement<Seq> createSeq(Seq value) {
        return new JAXBElement<Seq>(_Seq_QNAME, Seq.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ref }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Ref }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "ref", substitutionHeadNamespace = "", substitutionHeadName = "media-object")
    public JAXBElement<Ref> createRef(Ref value) {
        return new JAXBElement<Ref>(_Ref_QNAME, Ref.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Audio }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Audio }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "audio", substitutionHeadNamespace = "", substitutionHeadName = "media-object")
    public JAXBElement<Audio> createAudio(Audio value) {
        return new JAXBElement<Audio>(_Audio_QNAME, Audio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Img }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Img }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "img", substitutionHeadNamespace = "", substitutionHeadName = "media-object")
    public JAXBElement<Img> createImg(Img value) {
        return new JAXBElement<Img>(_Img_QNAME, Img.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Video }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Video }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "video", substitutionHeadNamespace = "", substitutionHeadName = "media-object")
    public JAXBElement<Video> createVideo(Video value) {
        return new JAXBElement<Video>(_Video_QNAME, Video.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Text }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "text", substitutionHeadNamespace = "", substitutionHeadName = "media-object")
    public JAXBElement<Text> createText(Text value) {
        return new JAXBElement<Text>(_Text_QNAME, Text.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Textstream }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Textstream }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "textstream", substitutionHeadNamespace = "", substitutionHeadName = "media-object")
    public JAXBElement<Textstream> createTextstream(Textstream value) {
        return new JAXBElement<Textstream>(_Textstream_QNAME, Textstream.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Animation }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Animation }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "animation", substitutionHeadNamespace = "", substitutionHeadName = "media-object")
    public JAXBElement<Animation> createAnimation(Animation value) {
        return new JAXBElement<Animation>(_Animation_QNAME, Animation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Link }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "a", substitutionHeadNamespace = "", substitutionHeadName = "inline-link")
    public JAXBElement<Link> createA(Link value) {
        return new JAXBElement<Link>(_A_QNAME, Link.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssocLink }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AssocLink }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "anchor", substitutionHeadNamespace = "", substitutionHeadName = "assoc-link")
    public JAXBElement<AssocLink> createAnchor(AssocLink value) {
        return new JAXBElement<AssocLink>(_Anchor_QNAME, AssocLink.class, null, value);
    }

}
