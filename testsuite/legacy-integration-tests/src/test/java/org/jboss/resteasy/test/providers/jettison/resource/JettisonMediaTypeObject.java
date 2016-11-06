package org.jboss.resteasy.test.providers.jettison.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JettisonMediaTypeObject {
    @XmlAttribute
    String name = "bill";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
