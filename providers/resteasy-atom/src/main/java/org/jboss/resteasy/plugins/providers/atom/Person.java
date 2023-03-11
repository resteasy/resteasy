package org.jboss.resteasy.plugins.providers.atom;

import java.net.URI;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * <p>
 * Per RFC4287:
 * </p>
 *
 * <pre>
 *   A Person construct is an element that describes a person,
 *   corporation, or similar entity (hereafter, 'person').
 *
 *   atomPersonConstruct =
 *     atomCommonAttributes,
 *     (element atom:name { text }
 *      &amp; element atom:uri { atomUri }?
 *      &amp; element atom:email { atomEmailAddress }?
 *      &amp; extensionElement*)
 *
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Person extends CommonAttributes {
    private String name;

    private URI uri;

    private String email;

    public Person() {
    }

    public Person(final String name) {
        this.name = name;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @XmlElement
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
