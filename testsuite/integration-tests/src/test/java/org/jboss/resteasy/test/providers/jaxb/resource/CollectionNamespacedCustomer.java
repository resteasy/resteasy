package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer", namespace = "http://customer.com")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionNamespacedCustomer {
   @XmlElement
   private String name;

   public CollectionNamespacedCustomer() {
   }

   public CollectionNamespacedCustomer(final String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
