package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class InheritanceCat extends InheritanceAnimal {

    public InheritanceCat() {
    }

    public InheritanceCat(final String name) {
        super(name);
    }
}
