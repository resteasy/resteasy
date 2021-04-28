package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class InheritanceCat extends InheritanceAnimal {

   public InheritanceCat() {
   }

   public InheritanceCat(final String name) {
      super(name);
   }
}
