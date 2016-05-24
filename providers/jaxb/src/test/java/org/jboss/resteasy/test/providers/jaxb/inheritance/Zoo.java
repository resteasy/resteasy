package org.jboss.resteasy.test.providers.jaxb.inheritance;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Zoo
{

   private List<Animal> animals;

   public Zoo()
   {
      animals = new ArrayList<Animal>();
   }

   @XmlElementRef
   public List<Animal> getAnimals()
   {
      return animals;
   }

   public void add(Animal animal)
   {
      animals.add(animal);
   }
}
