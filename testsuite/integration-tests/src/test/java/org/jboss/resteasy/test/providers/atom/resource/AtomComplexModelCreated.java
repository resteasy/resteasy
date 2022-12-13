package org.jboss.resteasy.test.providers.atom.resource;

import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "created")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelCreated {

    @XmlElement
    private Date value;

    public Date getValue() {
        return value;
    }

    public void setValue(Date created) {
        value = created;
    }

}
