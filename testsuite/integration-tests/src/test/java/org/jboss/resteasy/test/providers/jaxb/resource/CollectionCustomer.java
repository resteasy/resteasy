package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionCustomer {
   @XmlElement
   private String name;

   public CollectionCustomer() {
   }

   public CollectionCustomer(final String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
