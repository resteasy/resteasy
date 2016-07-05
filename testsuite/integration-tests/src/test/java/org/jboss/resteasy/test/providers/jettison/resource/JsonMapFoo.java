package org.jboss.resteasy.test.providers.jettison.resource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JsonMapFoo {
    @XmlAttribute
    private String name;

    public JsonMapFoo() {
    }

    public JsonMapFoo(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
