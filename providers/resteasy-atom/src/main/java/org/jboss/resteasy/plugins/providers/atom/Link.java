package org.jboss.resteasy.plugins.providers.atom;

import java.net.URI;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Per RFC4287:
 * </p>
 *
 * <pre>
 *  The "atom:link" element defines a reference from an entry or feed to
 *  a Web resource.  This specification assigns no meaning to the content
 *  (if any) of this element.
 *
 *  atomLink =
 *     element atom:link {
 *        atomCommonAttributes,
 *        attribute href { atomUri },
 *        attribute rel { atomNCName | atomUri }?,
 *        attribute type { atomMediaType }?,
 *        attribute hreflang { atomLanguageTag }?,
 *        attribute title { text }?,
 *        attribute length { text }?,
 *        undefinedContent
 *     }
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Link extends CommonAttributes {
    protected URI href;

    protected String rel;

    protected MediaType type;

    protected String hreflang;

    protected String title;

    protected String length;

    public Link() {
    }

    public Link(final String rel, final URI href) {
        this.rel = rel;
        this.href = href;
    }

    public Link(final String rel, final URI href, final MediaType type) {
        this.rel = rel;
        this.href = href;
        this.type = type;
    }

    public Link(final String rel, final String href) {
        this.rel = rel;
        this.href = URI.create(href);
    }

    public Link(final String rel, final String href, final MediaType type) {
        this.rel = rel;
        this.href = URI.create(href);
        this.type = type;
    }

    public Link(final String rel, final String href, final String type) {
        this.rel = rel;
        this.href = URI.create(href);
        this.type = MediaType.valueOf(type);
    }

    @XmlAttribute(required = true)
    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    @XmlAttribute
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    @XmlAttribute
    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    @XmlAttribute
    public String getHreflang() {
        return hreflang;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    @XmlAttribute
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlAttribute
    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
