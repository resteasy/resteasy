package org.jboss.resteasy.test.xxe.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExternalParameterEntityWrapper {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
