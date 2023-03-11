package org.jboss.resteasy.plugins.providers.atom;

import java.net.URI;

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
 *  The "atom:category" element conveys information about a category
 *  associated with an entry or feed.  This specification assigns no
 *  meaning to the content (if any) of this element.
 *
 *  atomCategory =
 *     element atom:category {
 *        atomCommonAttributes,
 *        attribute term { text },
 *        attribute scheme { atomUri }?,
 *        attribute label { text }?,
 *        undefinedContent
 *     }
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Category extends CommonAttributes {
    private String term;

    private URI scheme;

    private String label;

    @XmlAttribute
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @XmlAttribute
    public URI getScheme() {
        return scheme;
    }

    public void setScheme(URI scheme) {
        this.scheme = scheme;
    }

    @XmlAttribute
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
