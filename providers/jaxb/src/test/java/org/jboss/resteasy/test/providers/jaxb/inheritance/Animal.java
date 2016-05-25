package org.jboss.resteasy.test.providers.jaxb.inheritance;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({Dog.class, Cat.class})
public abstract class Animal
{
   @XmlAttribute
   public String name;

   public Animal()
   {
      super();
   }

   public Animal(String name)
   {
      this.name = name;
   }
}
