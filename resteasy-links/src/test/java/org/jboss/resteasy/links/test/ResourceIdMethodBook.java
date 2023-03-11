package org.jboss.resteasy.links.test;

import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.links.ResourceID;

@XmlRootElement
public class ResourceIdMethodBook extends IdBook {

    private String name;

    public ResourceIdMethodBook() {
    }

    public ResourceIdMethodBook(final String name) {
        this.name = name;
    }

    @ResourceID
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
