package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionCustomer {
    @XmlElement
    private String name;

    public CollectionCustomer() {
    }

    public CollectionCustomer(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
