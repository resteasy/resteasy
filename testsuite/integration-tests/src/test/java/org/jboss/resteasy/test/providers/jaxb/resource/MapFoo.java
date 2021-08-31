package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://foo.com")
public class MapFoo {
   @XmlAttribute
   private String name;

   public MapFoo() {
   }

   public MapFoo(final String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
