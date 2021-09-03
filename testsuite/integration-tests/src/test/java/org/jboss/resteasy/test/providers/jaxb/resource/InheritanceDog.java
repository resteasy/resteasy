package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class InheritanceDog extends InheritanceAnimal {

   public InheritanceDog() {
   }

   public InheritanceDog(final String name) {
      super(name);
   }
}
