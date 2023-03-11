package org.jboss.resteasy.links.test;

import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.links.ResourceIDs;

@XmlRootElement
@ResourceIDs({ "namea", "nameb" })
public class ResourceIdsBook extends IdBook {

    private String namea;
    private String nameb;

    public ResourceIdsBook() {
    }

    public ResourceIdsBook(final String namea, final String nameb) {
        this.namea = namea;
        this.nameb = nameb;
    }

}
