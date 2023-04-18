package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BadContentTypeTestBean {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
