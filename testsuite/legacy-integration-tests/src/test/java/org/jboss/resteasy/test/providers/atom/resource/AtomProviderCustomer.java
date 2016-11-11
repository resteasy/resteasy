package org.jboss.resteasy.test.providers.atom.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer", namespace = "http://jboss.org/Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomProviderCustomer {
    @XmlElement
    private String name;

    public AtomProviderCustomer() {
    }

    public AtomProviderCustomer(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
