package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ InheritanceDog.class, InheritanceCat.class })
public abstract class InheritanceAnimal {
    @XmlAttribute
    public String name;

    public InheritanceAnimal() {
    }

    public InheritanceAnimal(final String name) {
        this.name = name;
    }
}
