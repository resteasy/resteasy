package org.jboss.resteasy.test.providers.multipart.resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
public class EmbeddedMultipartCustomer {
    private String name;

    public EmbeddedMultipartCustomer() {
    }

    public EmbeddedMultipartCustomer(final String name) {
        this.name = name;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
