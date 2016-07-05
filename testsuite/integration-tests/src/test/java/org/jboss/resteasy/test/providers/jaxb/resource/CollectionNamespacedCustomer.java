package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer", namespace = "http://customer.com")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionNamespacedCustomer {
    @XmlElement
    private String name;

    public CollectionNamespacedCustomer() {
    }

    public CollectionNamespacedCustomer(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}