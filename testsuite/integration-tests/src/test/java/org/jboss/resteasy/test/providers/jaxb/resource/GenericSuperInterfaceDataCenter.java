package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericSuperInterfaceDataCenter extends GenericSuperInterfaceBaseResource {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
