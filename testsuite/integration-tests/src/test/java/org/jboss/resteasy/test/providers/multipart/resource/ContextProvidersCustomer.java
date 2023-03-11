package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
