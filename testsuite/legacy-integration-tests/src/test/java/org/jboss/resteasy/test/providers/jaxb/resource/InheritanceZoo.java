package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
