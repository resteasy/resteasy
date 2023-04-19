package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class InheritanceDog extends InheritanceAnimal {

    public InheritanceDog() {
    }

    public InheritanceDog(final String name) {
        super(name);
    }
}
