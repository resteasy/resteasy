package org.jboss.resteasy.test.providers.multipart.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextProvidersCustomer {
    @XmlElement
    private String name;

    public ContextProvidersCustomer() {
    }

    public ContextProvidersCustomer(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
