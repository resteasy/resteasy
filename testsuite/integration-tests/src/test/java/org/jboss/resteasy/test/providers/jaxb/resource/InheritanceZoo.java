package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InheritanceZoo {

    private List<InheritanceAnimal> animals;

    public InheritanceZoo() {
        animals = new ArrayList<InheritanceAnimal>();
    }

    @XmlElementRef
    public List<InheritanceAnimal> getAnimals() {
        return animals;
    }

    public void add(InheritanceAnimal animal) {
        animals.add(animal);
    }
}
