package org.jboss.resteasy.test.providers.multipart.resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
public class MimeMultipartProviderCustomer {
    private String name;

    public MimeMultipartProviderCustomer() {
    }

    public MimeMultipartProviderCustomer(final String name) {
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
