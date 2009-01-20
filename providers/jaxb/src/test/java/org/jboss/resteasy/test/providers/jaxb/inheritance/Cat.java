package org.jboss.resteasy.test.providers.jaxb.inheritance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Cat extends Animal
{

   public Cat()
   {
      super();
   }

   public Cat(String name)
   {
      super(name);
   }
}
