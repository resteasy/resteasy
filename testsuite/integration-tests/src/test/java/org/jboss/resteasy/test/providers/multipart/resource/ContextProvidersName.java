package org.jboss.resteasy.test.providers.multipart.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "name")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextProvidersName {
    @XmlElement
    private String name;

    public ContextProvidersName() {
    }

    public ContextProvidersName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ContextProvidersName)) {
            return false;
        }
        return name.equals(((ContextProvidersName) o).getName());
    }

    public int hashCode() {
        return super.hashCode();
    }
}
