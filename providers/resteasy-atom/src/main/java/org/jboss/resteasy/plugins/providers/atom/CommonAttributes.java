package org.jboss.resteasy.plugins.providers.atom;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Attributes common across all atom types
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CommonAttributes {
    private String language;
    private URI base;

    private Map extensionAttributes = new HashMap();

    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    public URI getBase() {
        return base;
    }

    public void setBase(URI base) {
        this.base = base;
    }

    @XmlAnyAttribute
    public Map getExtensionAttributes() {
        return extensionAttributes;
    }
}
