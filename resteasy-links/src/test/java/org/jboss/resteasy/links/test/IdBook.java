package org.jboss.resteasy.links.test;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.links.RESTServiceDiscovery;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class IdBook {
    @XmlElement
    private RESTServiceDiscovery rest;

    public RESTServiceDiscovery getRest() {
        return rest;
    }

    public void setRest(RESTServiceDiscovery rest) {
        this.rest = rest;
    }
}
