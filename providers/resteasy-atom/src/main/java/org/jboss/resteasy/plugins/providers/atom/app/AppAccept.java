package org.jboss.resteasy.plugins.providers.atom.app;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Per RFC5023:
 * </p>
 * 
 * <pre>
 * The content of an "app:accept" element value is a media range as
 * defined in [RFC2616].  The media range specifies a type of
 * representation that can be POSTed to a Collection.
 * 
 * The app:accept element is similar to the HTTP Accept request-header
 * [RFC2616].  Media type parameters are allowed within app:accept, but
 * app:accept has no notion of preference -- "accept-params" or "q"
 * arguments, as specified in Section 14.1 of [RFC2616] are not
 * significant.
 * 
 * White space (as defined in [REC-xml]) around the app:accept element's
 * media range is insignificant and MUST be ignored.
 * 
 * A value of "application/atom+xml;type=entry" MAY appear in any app:
 * accept list of media ranges and indicates that Atom Entry Documents
 * can be POSTed to the Collection.  If no app:accept element is
 * present, clients SHOULD treat this as equivalent to an app:accept
 * element with the content "application/atom+xml;type=entry".
 * 
 * If one app:accept element exists and is empty, clients SHOULD assume
 * that the Collection does not support the creation of new Entries.
 * 
 * appAccept =
 *    element app:accept {
 *          appCommonAttributes,
 *          ( text? )
 *    }
 * </pre>
 * 
 * @author <a href="mailto:kurt.stam@gmail.com">Kurt Stam</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "accept")
public class AppAccept extends AppCommonAttributes
{

    private static final long serialVersionUID = 8792589507058023990L;
    @XmlValue
    protected String content;

    public AppAccept() {}
    
    public AppAccept(String content) {
        super();
        this.content = content;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }
}
