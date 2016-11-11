package org.jboss.resteasy.test.providers.jettison.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TypeMappingBean {
    private String name;

    public TypeMappingBean() {

    }

    public TypeMappingBean(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
