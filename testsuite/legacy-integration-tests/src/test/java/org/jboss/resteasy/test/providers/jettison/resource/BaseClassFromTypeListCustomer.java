package org.jboss.resteasy.test.providers.jettison.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseClassFromTypeListCustomer {
    @XmlElement
    private String name;

    public BaseClassFromTypeListCustomer() {
    }

    public BaseClassFromTypeListCustomer(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
